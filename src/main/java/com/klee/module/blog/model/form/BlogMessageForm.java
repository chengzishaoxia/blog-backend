package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "留言表单")
public class BlogMessageForm implements Serializable {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    @Schema(description = "留言昵称")
    private String nickname;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 120, message = "留言内容长度不能超过120个字符")
    @Schema(description = "留言内容")
    private String content;
}
