package com.klee.module.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogCarousel;
import com.klee.module.blog.model.form.BlogCarouselForm;
import com.klee.module.blog.model.query.BlogCarouselPageQuery;
import com.klee.module.blog.model.vo.BlogCarouselVO;

import java.util.List;

public interface BlogCarouselService extends IService<BlogCarousel> {

    boolean saveCarousel(BlogCarouselForm form);

    boolean updateCarousel(Long id, BlogCarouselForm form);

    boolean deleteCarousel(Long id);

    IPage<BlogCarouselVO> pageCarousels(BlogCarouselPageQuery queryParams);

    List<BlogCarouselVO> listEnabled();
}

