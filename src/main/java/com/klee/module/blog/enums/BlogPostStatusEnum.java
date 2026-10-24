package com.klee.module.blog.enums;

import lombok.Getter;

/** 博客文章状态 */
@Getter
public enum BlogPostStatusEnum {
    DRAFT(0),
    PUBLISHED(1);
    private final int value;
    BlogPostStatusEnum(int value){ this.value = value; }
}

