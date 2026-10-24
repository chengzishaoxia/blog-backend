package com.klee.system.enums;

import lombok.Getter;

/**
 * 系统内角色编码枚举，集中管理角色字符串，避免散落硬编码。
 */
@Getter
public enum RoleCodeEnum {

    ROOT("ROOT"),
    ADMIN("ADMIN"),
    GUEST("GUEST");

    private final String code;

    RoleCodeEnum(String code) {
        this.code = code;
    }

}

