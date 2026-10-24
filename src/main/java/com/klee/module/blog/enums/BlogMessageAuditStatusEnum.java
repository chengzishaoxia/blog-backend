package com.klee.module.blog.enums;

import lombok.Getter;

@Getter
public enum BlogMessageAuditStatusEnum {
    PENDING(0),
    APPROVED(1),
    REJECTED(2);

    private final int value;

    BlogMessageAuditStatusEnum(int value) {
        this.value = value;
    }
}
