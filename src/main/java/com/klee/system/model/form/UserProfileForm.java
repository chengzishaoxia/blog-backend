package com.klee.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户资料表单
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Schema(description = "用户资料表单")
@Data
public class UserProfileForm {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像文件ID")
    private Long avatarFileId; // 原 avatar 重命名

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;


}
