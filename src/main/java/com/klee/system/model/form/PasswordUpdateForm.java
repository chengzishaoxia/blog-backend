package com.klee.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户密码修改表单
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Schema(description = "用户密码修改表单")
@Data
public class PasswordUpdateForm {

    @Schema(description = "原密码")
    private String oldPassword;

    @Schema(description = "新密码")
    private String newPassword;

    @Schema(description = "确认密码")
    private String confirmPassword;
}
