package com.klee.shared.file.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.model.vo.FileInfoPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FileInfoConverter {

    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    FileInfoPageVO toPageVo(FileInfo entity);

    Page<FileInfoPageVO> toPageVo(Page<FileInfo> page);

    @InheritInverseConfiguration(name = "toPageVo")
    FileInfo toEntity(FileInfoPageVO vo);
}

