package com.klee.module.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogTag;
import com.klee.module.blog.model.form.BlogTagForm;
import com.klee.module.blog.model.query.BlogTagPageQuery;
import com.klee.module.blog.model.vo.BlogTagVO;

public interface BlogTagService extends IService<BlogTag> {


    IPage<BlogTagVO> pageTags(BlogTagPageQuery queryParams);

    boolean saveTag(BlogTagForm form);

    boolean updateTag(Long id, BlogTagForm form);

    boolean deleteTag(Long id);
}
