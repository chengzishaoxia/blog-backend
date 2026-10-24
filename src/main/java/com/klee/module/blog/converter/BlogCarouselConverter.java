package com.klee.module.blog.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.module.blog.model.entity.BlogCarousel;
import com.klee.module.blog.model.form.BlogCarouselForm;
import com.klee.module.blog.model.vo.BlogCarouselVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BlogCarouselConverter {

    @Mappings({
            @Mapping(target = "fileUrl", ignore = true) // 后续填充
    })
    BlogCarouselVO toVo(BlogCarousel entity);

    Page<BlogCarouselVO> toVo(Page<BlogCarousel> page);

    BlogCarouselForm toForm(BlogCarousel entity);

    @InheritInverseConfiguration(name = "toForm")
    BlogCarousel toEntity(BlogCarouselForm form);
}

