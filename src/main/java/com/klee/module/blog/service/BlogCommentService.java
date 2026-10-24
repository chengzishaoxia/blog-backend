package com.klee.module.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogComment;
import com.klee.module.blog.model.form.BlogCommentForm;
import com.klee.module.blog.model.vo.BlogCommentVO;
import com.klee.module.blog.model.vo.BlogRecentCommentVO;

import java.util.List;

public interface BlogCommentService extends IService<BlogComment> {

    List<BlogCommentVO> listCommentsByPost(Long postId);

    List<BlogRecentCommentVO> listRecentComments(int limit);

    boolean saveComment(BlogCommentForm form);

    boolean updateComment(Long id, BlogCommentForm form);

    boolean deleteComment(Long id);
}

