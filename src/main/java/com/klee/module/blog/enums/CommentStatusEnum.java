package com.klee.module.blog.enums;

import lombok.Getter;

/** 评论显示状态 */
@Getter
public enum CommentStatusEnum {
    HIDDEN(0),
    VISIBLE(1);
    private final int value;
    CommentStatusEnum(int value){ this.value = value; }
}

