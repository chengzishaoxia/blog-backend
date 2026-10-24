package com.klee.shared.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.shared.file.model.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件信息 Mapper
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
}

