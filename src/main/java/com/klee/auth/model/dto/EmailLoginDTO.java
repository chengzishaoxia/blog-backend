package com.klee.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "邮箱密码登录参数")
public class EmailLoginDTO {

    @Schema(description = "邮箱地址", example = "demo@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "登录密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

}