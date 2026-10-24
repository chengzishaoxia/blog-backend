package com.klee.module.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogPost;
import com.klee.module.blog.model.form.BlogPostForm;
import com.klee.module.blog.model.query.BlogPostPageQuery;
import com.klee.module.blog.model.vo.BlogPostVO;
import com.klee.module.blog.model.vo.BlogArchiveGroupVO;

import java.util.List;

public interface BlogPostService extends IService<BlogPost> {

    IPage<BlogPostVO> page(BlogPostPageQuery query);

    BlogPostVO getPost(Long id, boolean increaseView);

    List<BlogPostVO> recommendList();

    boolean savePost(BlogPostForm form);

    boolean updatePost(Long id, BlogPostForm form);

    boolean deletePost(Long id);

    boolean upsertPost(BlogPostForm form);

    /**
     * 文章归档（按年份-月份分组）
     */
    List<BlogArchiveGroupVO> listArchives();
}
