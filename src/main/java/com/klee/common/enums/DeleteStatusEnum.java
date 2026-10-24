package com.klee.common.enums;

import lombok.Getter;

/** 逻辑删除状态 */
@Getter
public enum DeleteStatusEnum {
    NOT_DELETED(0),
    DELETED(1);
    private final int value;
    DeleteStatusEnum(int value){ this.value = value; }
}
