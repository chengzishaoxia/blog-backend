package com.klee.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱更新表单
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Schema(description = "邮箱更新表单")
@Data
public class EmailUpdateForm {

    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String code;

}
