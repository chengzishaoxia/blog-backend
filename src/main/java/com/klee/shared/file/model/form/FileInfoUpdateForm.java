package com.klee.shared.file.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文件信息更新表单
 */
@Data
@Schema(description = "文件信息更新表单")
public class FileInfoUpdateForm {

    @Schema(description = "文件名称")
    @Size(max = 255)
    private String name;

    @Schema(description = "用途类型 如: AVATAR, EMOJI, BLOG_COVER, CAROUSEL, OTHER")
    @Size(max = 50)
    private String usageType;
}

