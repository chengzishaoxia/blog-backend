package com.klee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 日志模块枚举
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(enumAsRef = true)
@Getter
public enum LogModuleEnum {

    EXCEPTION("异常"),
    LOGIN("登录"),
    USER("用户"),
    DEPT("部门"),
    ROLE("角色"),
    MENU("菜单"),
    DICT("字典"),
    SETTING("系统配置"),
    BLOG("博客"), // 新增博客模块
    CHAT("聊天"),
    OTHER("其它");

    @JsonValue
    private final String moduleName;

    LogModuleEnum(String moduleName) {
        this.moduleName = moduleName;
    }
}
