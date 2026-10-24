package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.blog.converter.BlogConverter;
import com.klee.module.blog.mapper.BlogCommentMapper;
import com.klee.module.blog.mapper.BlogPostMapper;
import com.klee.module.blog.model.entity.BlogComment;
import com.klee.module.blog.model.form.BlogCommentForm;
import com.klee.module.blog.model.vo.BlogCommentVO;
import com.klee.module.blog.model.vo.BlogRecentCommentVO;
import com.klee.module.blog.service.BlogCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import com.klee.system.service.UserService;
import com.klee.system.model.entity.User;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.module.blog.enums.CommentStatusEnum;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import jakarta.annotation.Resource;

@Service
@RequiredArgsConstructor
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {

    private final BlogConverter converter;
    private final UserService userService;
    private final BlogPostMapper blogPostMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    public List<BlogCommentVO> listCommentsByPost(Long postId) {
        List<BlogComment> all = this.list(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getPostId, postId)
                .eq(BlogComment::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .orderByAsc(BlogComment::getCreateTime));
        if (CollectionUtils.isEmpty(all)) return Collections.emptyList();
        Set<Long> userIds = all.stream().map(BlogComment::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = CollectionUtils.isEmpty(userIds) ? Collections.emptyMap() : userService.list(new LambdaQueryWrapper<User>()
                .in(User::getId, userIds)
                .select(User::getId, User::getNickname, User::getAvatarFileId) // 修复: 使用 avatarFileId
        ).stream().collect(Collectors.toMap(User::getId, u -> u));
        List<Long> avatarFileIds = userMap.values().stream().map(User::getAvatarFileId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> avatarUrlMap = avatarFileIds.isEmpty() ? Map.of() : fileInfoMapper.selectBatchIds(avatarFileIds)
                .stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
        Map<Long, List<BlogComment>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(BlogComment::getParentId));
        List<BlogCommentVO> roots = new ArrayList<>();
        for (BlogComment c : all) {
            if (c.getParentId() == null || c.getParentId() == 0) {
                BlogCommentVO vo = converter.toCommentVo(c);
                fillUser(vo, userMap, avatarUrlMap);
                attachChildren(vo, childrenMap, userMap, avatarUrlMap);
                roots.add(vo);
            }
        }
        return roots;
    }

    @Override
    public List<BlogRecentCommentVO> listRecentComments(int limit) {
        List<BlogComment> comments = this.list(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .eq(BlogComment::getStatus, CommentStatusEnum.VISIBLE.getValue())
                .orderByDesc(BlogComment::getCreateTime)
                .last("limit " + Math.max(1, limit)));
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }

        Set<Long> userIds = comments.stream().map(BlogComment::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = CollectionUtils.isEmpty(userIds) ? Collections.emptyMap() : userService.list(new LambdaQueryWrapper<User>()
                .in(User::getId, userIds)
                .select(User::getId, User::getNickname, User::getAvatarFileId))
                .stream()
                .collect(Collectors.toMap(User::getId, item -> item));
        List<Long> avatarFileIds = userMap.values().stream().map(User::getAvatarFileId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> avatarUrlMap = avatarFileIds.isEmpty() ? Map.of() : fileInfoMapper.selectBatchIds(avatarFileIds)
                .stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));

        Set<Long> postIds = comments.stream().map(BlogComment::getPostId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> postTitleMap = postIds.isEmpty() ? Map.of() : blogPostMapper.selectBatchIds(postIds)
                .stream()
                .collect(Collectors.toMap(post -> post.getId(), post -> Optional.ofNullable(post.getTitle()).orElse("")));

        List<BlogRecentCommentVO> result = new ArrayList<>(comments.size());
        for (BlogComment comment : comments) {
            BlogRecentCommentVO vo = new BlogRecentCommentVO();
            vo.setId(comment.getId());
            vo.setPostId(comment.getPostId());
            vo.setPostTitle(postTitleMap.get(comment.getPostId()));
            vo.setContent(comment.getContent());
            vo.setCreateTime(comment.getCreateTime());
            if (comment.getUserId() != null) {
                User user = userMap.get(comment.getUserId());
                if (user != null) {
                    vo.setNickname(user.getNickname());
                    if (user.getAvatarFileId() != null) {
                        vo.setAvatar(avatarUrlMap.get(user.getAvatarFileId()));
                    }
                }
            }
            if (vo.getNickname() == null) {
                vo.setNickname("游客");
            }
            result.add(vo);
        }
        return result;
    }

    private void attachChildren(BlogCommentVO parent, Map<Long, List<BlogComment>> map, Map<Long, User> userMap, Map<Long, String> avatarUrlMap) {
        List<BlogComment> cs = map.get(parent.getId());
        if (cs == null) return;
        List<BlogCommentVO> childVos = converter.toCommentVo(cs);
        for (BlogCommentVO vo : childVos) {
            fillUser(vo, userMap, avatarUrlMap);
            attachChildren(vo, map, userMap, avatarUrlMap);
        }
        parent.setChildren(childVos);
    }

    private void fillUser(BlogCommentVO vo, Map<Long, User> userMap, Map<Long, String> avatarUrlMap) {
        if (vo == null || CollectionUtils.isEmpty(userMap.values())) return;
        User u = userMap.get(vo.getUserId());
        if (u != null) {
            vo.setNickname(u.getNickname());
            if (u.getAvatarFileId() != null) {
                vo.setAvatar(avatarUrlMap.get(u.getAvatarFileId()));
            }
        }
    }

    @Override
    @Transactional
    public boolean saveComment(BlogCommentForm form) {
        BlogComment entity = converter.toCommentEntity(form);
        entity.setId(null);
        entity.setUserId(SecurityUtils.getUserId());
        entity.setLikeCount(0L);
        entity.setStatus(CommentStatusEnum.VISIBLE.getValue());
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        if (entity.getParentId() == null) entity.setParentId(0L);
        return this.save(entity);
    }

    @Override
    public boolean updateComment(Long id, BlogCommentForm form) {
        BlogComment entity = converter.toCommentEntity(form);
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public boolean deleteComment(Long id) {
        return this.update(new LambdaUpdateWrapper<BlogComment>().eq(BlogComment::getId, id).set(BlogComment::getIsDeleted, DeleteStatusEnum.DELETED.getValue()));
    }
}
