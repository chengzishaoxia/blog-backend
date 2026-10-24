package com.klee.core.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 认证令牌响应对象 (Sa-Token 版本，已去除 refreshToken)
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@Schema(description = "认证令牌响应对象")
@Data
@Builder
public class AuthenticationToken {

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken; // 恢复 refreshToken

    @Schema(description = "过期时间(单位：秒，-1 表示永不过期)")
    private Integer expiresIn;
}
