package com.klee.shared.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.model.form.FileInfoUpdateForm;
import com.klee.shared.file.model.query.FileInfoPageQuery;
import com.klee.shared.file.model.vo.FileInfoPageVO; // 新增: VO 引入

import java.util.List;

/**
 * 文件信息管理服务
 */
public interface FileInfoService {

    IPage<FileInfo> page(FileInfoPageQuery query);

    FileInfo get(Long id);

    boolean update(Long id, FileInfoUpdateForm form);

    boolean delete(Long id);

    boolean batchDelete(List<Long> ids);

    // 新增: 返回已补充用户名的分页 VO
    Page<FileInfoPageVO> pageVo(FileInfoPageQuery query);

    // 新增: 返回单个补充用户名的 VO
    FileInfoPageVO getVo(Long id);
}
