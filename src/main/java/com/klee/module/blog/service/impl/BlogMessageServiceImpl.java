package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.common.exception.BusinessException;
import com.klee.common.util.IPUtils;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.blog.converter.BlogConverter;
import com.klee.module.blog.enums.BlogMessageAuditStatusEnum;
import com.klee.module.blog.mapper.BlogMessageMapper;
import com.klee.module.blog.model.entity.BlogMessage;
import com.klee.module.blog.model.form.BlogMessageAuditForm;
import com.klee.module.blog.model.form.BlogMessageForm;
import com.klee.module.blog.model.query.BlogMessagePageQuery;
import com.klee.module.blog.model.vo.BlogMessageDanmakuVO;
import com.klee.module.blog.model.vo.BlogMessageVO;
import com.klee.module.blog.service.BlogMessageService;
import com.klee.shared.file.enums.FileUsageTypeEnum;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.system.model.entity.User;
import com.klee.system.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogMessageServiceImpl extends ServiceImpl<BlogMessageMapper, BlogMessage> implements BlogMessageService {

    private final BlogConverter converter;
    private final UserService userService;

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    public IPage<BlogMessageVO> pageMessages(BlogMessagePageQuery query) {
        Page<BlogMessage> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()),
                new LambdaQueryWrapper<BlogMessage>()
                        .eq(BlogMessage::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                        .like(StringUtils.isNotBlank(query.getKeywords()), BlogMessage::getContent, query.getKeywords())
                        .eq(query.getAuditStatus() != null, BlogMessage::getAuditStatus, query.getAuditStatus())
                        .orderByDesc(BlogMessage::getCreateTime));

        Page<BlogMessageVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<BlogMessageVO> records = converter.toMessageVo(page.getRecords());
        fillUserInfo(records);
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    @Transactional
    public boolean submitMessage(BlogMessageForm form, String submitIp) {
        String ip = StringUtils.trimToEmpty(submitIp);
        if (StringUtils.isBlank(ip)) {
            throw new BusinessException("无法识别当前请求IP，请稍后重试");
        }
        if (this.lambdaQuery().eq(BlogMessage::getSubmitIp, ip).count() > 0) {
            throw new BusinessException("当前IP已提交过留言，请勿重复提交");
        }

        BlogMessage entity = converter.toMessageEntity(form);
        entity.setId(null);
    Long userId = SecurityUtils.getUserId();
    entity.setUserId(userId);
    entity.setAvatarFileId(resolveSubmitAvatarFileId(userId));
        entity.setNickname(StringUtils.trimToEmpty(form.getNickname()));
        entity.setSubmitIp(ip);
        entity.setIpRegion(StringUtils.trimToNull(IPUtils.getRegion(ip)));
        entity.setAuditStatus(BlogMessageAuditStatusEnum.PENDING.getValue());
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        entity.setAuditBy(null);
        entity.setAuditTime(null);
        entity.setAuditRemark(null);
        return this.save(entity);
    }

    @Override
    public boolean auditMessage(Long id, BlogMessageAuditForm form) {
        if (!Objects.equals(form.getAuditStatus(), BlogMessageAuditStatusEnum.APPROVED.getValue())
                && !Objects.equals(form.getAuditStatus(), BlogMessageAuditStatusEnum.REJECTED.getValue())) {
            return false;
        }
        return this.update(new LambdaUpdateWrapper<BlogMessage>()
                .eq(BlogMessage::getId, id)
                .eq(BlogMessage::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .set(BlogMessage::getAuditStatus, form.getAuditStatus())
                .set(BlogMessage::getAuditRemark, StringUtils.trimToNull(form.getAuditRemark()))
                .set(BlogMessage::getAuditBy, SecurityUtils.getUserId())
                .set(BlogMessage::getAuditTime, LocalDateTime.now()));
    }

    @Override
    public boolean deleteMessage(Long id) {
        return this.update(new LambdaUpdateWrapper<BlogMessage>()
                .eq(BlogMessage::getId, id)
                .set(BlogMessage::getIsDeleted, DeleteStatusEnum.DELETED.getValue()));
    }

    @Override
    public List<BlogMessageDanmakuVO> listApprovedDanmaku(Integer limit) {
        int size = limit == null ? 120 : Math.max(1, Math.min(limit, 300));
        List<BlogMessage> list = this.list(new LambdaQueryWrapper<BlogMessage>()
                .eq(BlogMessage::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .eq(BlogMessage::getAuditStatus, BlogMessageAuditStatusEnum.APPROVED.getValue())
                .orderByDesc(BlogMessage::getCreateTime)
                .last("limit " + size));

        List<BlogMessageDanmakuVO> danmaku = converter.toMessageDanmakuVo(list);
        Map<Long, User> userMap = buildUserMap(list);
        List<FileInfo> fallbackAvatars = listAvailableAvatarFiles();
        Map<Long, String> avatarUrlMap = buildAvatarUrlMap(list, userMap, fallbackAvatars);
        Map<Long, BlogMessage> sourceMap = list.stream().collect(Collectors.toMap(BlogMessage::getId, m -> m));
        for (BlogMessageDanmakuVO item : danmaku) {
            BlogMessage source = sourceMap.get(item.getId());
            if (source != null) {
                User user = source.getUserId() == null ? null : userMap.get(source.getUserId());
                item.setNickname(resolveAuthorName(source.getNickname(), user));
                item.setAvatar(avatarUrlMap.get(resolveAvatarFileId(source, user, fallbackAvatars)));
            }
        }
        Collections.reverse(danmaku);
        return danmaku;
    }

    private void fillUserInfo(List<BlogMessageVO> records) {
        if (CollectionUtils.isEmpty(records)) return;

        Set<Long> userIds = new HashSet<>();
        for (BlogMessageVO item : records) {
            if (item.getUserId() != null) userIds.add(item.getUserId());
            if (item.getAuditBy() != null) userIds.add(item.getAuditBy());
        }
        if (CollectionUtils.isEmpty(userIds)) return;

        Map<Long, User> userMap = userService.list(new LambdaQueryWrapper<User>()
                        .in(User::getId, userIds)
                        .select(User::getId, User::getNickname, User::getUsername, User::getAvatarFileId))
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Long> avatarIds = userMap.values().stream()
                .map(User::getAvatarFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<FileInfo> fallbackAvatars = listAvailableAvatarFiles();
        Set<Long> allAvatarIds = new HashSet<>(avatarIds);
        records.stream().map(BlogMessageVO::getAvatarFileId).filter(Objects::nonNull).forEach(allAvatarIds::add);
        fallbackAvatars.stream().map(FileInfo::getId).forEach(allAvatarIds::add);
        Map<Long, String> avatarUrlMap = allAvatarIds.isEmpty()
                ? Map.of()
                : fileInfoMapper.selectBatchIds(allAvatarIds).stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));

        for (BlogMessageVO item : records) {
            User author = userMap.get(item.getUserId());
            if (author != null) {
                item.setNickname(StringUtils.defaultIfBlank(item.getNickname(), StringUtils.defaultIfBlank(author.getNickname(), author.getUsername())));
            } else {
                item.setNickname(StringUtils.defaultIfBlank(item.getNickname(), "游客"));
            }
            item.setAvatar(avatarUrlMap.get(resolveAvatarFileId(item.getAvatarFileId(), author == null ? null : author.getAvatarFileId(), item.getId(), fallbackAvatars)));

            User auditor = userMap.get(item.getAuditBy());
            if (auditor != null) {
                item.setAuditByName(StringUtils.defaultIfBlank(auditor.getNickname(), auditor.getUsername()));
            }
        }
    }

    private Map<Long, User> buildUserMap(List<BlogMessage> list) {
        Set<Long> userIds = list.stream().map(BlogMessage::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) return Map.of();
        return userService.list(new LambdaQueryWrapper<User>()
                        .in(User::getId, userIds)
                        .select(User::getId, User::getNickname, User::getUsername, User::getAvatarFileId))
                .stream().collect(Collectors.toMap(User::getId, u -> u));
    }

    private Map<Long, String> buildAvatarUrlMap(List<BlogMessage> list, Map<Long, User> userMap, List<FileInfo> fallbackAvatars) {
        Set<Long> avatarIds = new HashSet<>();
        list.stream().map(BlogMessage::getAvatarFileId).filter(Objects::nonNull).forEach(avatarIds::add);
        userMap.values().stream().map(User::getAvatarFileId).filter(Objects::nonNull).forEach(avatarIds::add);
        fallbackAvatars.stream().map(FileInfo::getId).forEach(avatarIds::add);
        if (avatarIds.isEmpty()) {
            return Map.of();
        }
        return fileInfoMapper.selectBatchIds(avatarIds).stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
    }

    private List<FileInfo> listAvailableAvatarFiles() {
        return fileInfoMapper.selectList(new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getUsageType, FileUsageTypeEnum.AVATAR.name())
                .eq(FileInfo::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .select(FileInfo::getId, FileInfo::getUrl));
    }

    private Long resolveSubmitAvatarFileId(Long userId) {
        if (userId != null) {
            User user = userService.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getId, userId)
                    .select(User::getId, User::getAvatarFileId));
            if (user != null && user.getAvatarFileId() != null) {
                return user.getAvatarFileId();
            }
        }
        return pickRandomAvatarFileId();
    }

    private Long pickRandomAvatarFileId() {
        List<FileInfo> avatarFiles = listAvailableAvatarFiles();
        if (CollectionUtils.isEmpty(avatarFiles)) {
            return null;
        }
        return avatarFiles.get(ThreadLocalRandom.current().nextInt(avatarFiles.size())).getId();
    }

    private Long resolveAvatarFileId(BlogMessage source, User user, List<FileInfo> fallbackAvatars) {
        return resolveAvatarFileId(
                source == null ? null : source.getAvatarFileId(),
                user == null ? null : user.getAvatarFileId(),
                source == null ? null : source.getId(),
                fallbackAvatars
        );
    }

    private Long resolveAvatarFileId(Long messageAvatarFileId, Long userAvatarFileId, Long seed, List<FileInfo> fallbackAvatars) {
        if (messageAvatarFileId != null) {
            return messageAvatarFileId;
        }
        if (userAvatarFileId != null) {
            return userAvatarFileId;
        }
        if (CollectionUtils.isEmpty(fallbackAvatars)) {
            return null;
        }
        int index = Math.floorMod(Objects.hashCode(seed), fallbackAvatars.size());
        return fallbackAvatars.get(index).getId();
    }

    private String resolveAuthorName(String storedNickname, User user) {
        if (StringUtils.isNotBlank(storedNickname)) {
            return storedNickname;
        }
        if (user != null) {
            return StringUtils.defaultIfBlank(user.getNickname(), user.getUsername());
        }
        return "游客";
    }
}
