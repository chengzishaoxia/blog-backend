package com.klee.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号更新表单
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Schema(description = "手机号更新表单")
@Data
public class MobileUpdateForm {

    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;

    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String code;

}
