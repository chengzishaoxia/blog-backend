package com.klee.shared.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.model.form.FileInfoUpdateForm;
import com.klee.shared.file.model.query.FileInfoPageQuery;
import com.klee.shared.file.model.vo.FileInfoPageVO; // 新增 VO
import com.klee.shared.file.service.FileInfoService;
import com.klee.shared.file.service.FileService; // 新增: 物理文件删除
import com.klee.shared.file.converter.FileInfoConverter; // 新增: 转换器
import com.klee.system.service.UserService; // 新增: 用户服务
import com.klee.system.model.entity.User; // 新增: 用户实体
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    private final FileInfoMapper fileInfoMapper;
    private final FileService fileService; // 新增注入
    private final FileInfoConverter fileInfoConverter; // 新增注入
    private final UserService userService; // 新增注入

    @Override
    public IPage<FileInfo> page(FileInfoPageQuery query) {
        Page<FileInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FileInfo> qw = buildWrapper(query);
        return fileInfoMapper.selectPage(page, qw);
    }

    private LambdaQueryWrapper<FileInfo> buildWrapper(FileInfoPageQuery query) {
        LambdaQueryWrapper<FileInfo> qw = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.isNotBlank(query.getKeywords())) {
                qw.like(FileInfo::getName, query.getKeywords().trim());
            }
            if (StringUtils.isNotBlank(query.getUsageType())) {
                qw.eq(FileInfo::getUsageType, query.getUsageType().trim());
            }
            if (query.getUploaderUserId() != null) {
                qw.eq(FileInfo::getUploaderUserId, query.getUploaderUserId());
            }
            if (query.getStartTime() != null) {
                qw.ge(FileInfo::getCreateTime, query.getStartTime());
            }
            if (query.getEndTime() != null) {
                qw.le(FileInfo::getCreateTime, query.getEndTime());
            }
            if (Boolean.TRUE.equals(query.getOnlyImages())) {
                qw.likeRight(FileInfo::getContentType, "image/");
            }
        }
        qw.orderByDesc(FileInfo::getCreateTime);
        return qw;
    }

    @Override
    public FileInfo get(Long id) {
        if (id == null) return null;
        return fileInfoMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean update(Long id, FileInfoUpdateForm form) {
        if (id == null || form == null) return false;
        FileInfo entity = fileInfoMapper.selectById(id);
        if (entity == null) return false;
        if (StringUtils.isNotBlank(form.getName())) {
            entity.setName(form.getName().trim());
        }
        if (StringUtils.isNotBlank(form.getUsageType())) {
            entity.setUsageType(form.getUsageType().trim().toUpperCase());
        }
        return fileInfoMapper.updateById(entity) == 1;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (id == null) return false;
        FileInfo fi = fileInfoMapper.selectById(id);
        if (fi == null) return false;
        // 物理文件删除(忽略失败不影响记录删除)
        if (StringUtils.isNotBlank(fi.getUrl())) {
            try { fileService.deleteFile(fi.getUrl()); } catch (Exception ignored) {}
        }
        return fileInfoMapper.deleteById(id) == 1;
    }

    @Override
    @Transactional
    public boolean batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return false;
        List<FileInfo> list = fileInfoMapper.selectBatchIds(ids);
        for (FileInfo fi : list) {
            if (fi != null && StringUtils.isNotBlank(fi.getUrl())) {
                try { fileService.deleteFile(fi.getUrl()); } catch (Exception ignored) {}
            }
        }
        return fileInfoMapper.deleteBatchIds(ids) > 0;
    }

    // 新增: 分页 VO (附带用户名)
    @Override
    public Page<FileInfoPageVO> pageVo(FileInfoPageQuery query) {
        IPage<FileInfo> page = this.page(query);
        Page<FileInfoPageVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<FileInfoPageVO> voList = page.getRecords().stream().map(fileInfoConverter::toPageVo).collect(Collectors.toList());
        enrichUploaderUsername(voList);
        voPage.setRecords(voList);
        return voPage;
    }

    // 新增: 单个 VO (附带用户名)
    @Override
    public FileInfoPageVO getVo(Long id) {
        FileInfo fi = this.get(id);
        if (fi == null) return null;
        FileInfoPageVO vo = fileInfoConverter.toPageVo(fi);
        enrichUploaderUsername(Collections.singletonList(vo));
        return vo;
    }

    private void enrichUploaderUsername(List<FileInfoPageVO> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> userIds = list.stream()
                .map(FileInfoPageVO::getUploaderUserId)
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) return;
        List<User> users = userService.listByIds(userIds);
        if (users == null || users.isEmpty()) return;
        Map<Long, String> idNameMap = users.stream().collect(Collectors.toMap(User::getId, User::getUsername));
        list.forEach(vo -> vo.setUploaderUsername(idNameMap.get(vo.getUploaderUserId())));
    }
}
