package com.klee.config.property;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 安全相关配置属性
 *
 * 仅保留路径白名单与匿名访问路径配置；Token 相关配置改用 Sa-Token 自带配置 (sa-token 前缀)。
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 安全白名单路径（完全绕过安全过滤器）
     */
    private String[] ignoreUrls = new String[0];

    /**
     * 非安全端点路径（允许匿名访问的API）
     */
    @NotEmpty
    private String[] unsecuredUrls;
}
