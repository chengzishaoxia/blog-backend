package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 博客标签
 */
@Getter
@Setter
@TableName("blog_tag")
public class BlogTag extends BaseEntity {

    private String name;
    private Integer sort;
    private Integer status; // 1 启用 0 停用
    private Integer isDeleted;
}

