package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章与标签关联
 */
@Getter
@Setter
@TableName("blog_post_tag")
public class BlogPostTag extends BaseEntity {

    private Long postId;
    private Long tagId;
    private Integer isDeleted;
}

