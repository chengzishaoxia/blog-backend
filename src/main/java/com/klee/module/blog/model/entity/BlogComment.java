package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 博客评论
 */
@Getter
@Setter
@TableName("blog_comment")
public class BlogComment extends BaseEntity {

    private Long postId;      // 文章ID
    private Long parentId;    // 父评论ID (0 表示根评论)
    private Long userId;      // 评论用户ID
    private String content;   // 评论内容
    private Long likeCount;   // 点赞数
    private Integer status;   // 状态 1-显示 0-隐藏
    private Integer isDeleted; // 逻辑删除
}
