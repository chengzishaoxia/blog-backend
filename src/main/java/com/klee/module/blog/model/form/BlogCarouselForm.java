package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 轮播图表单
 */
@Data
@Schema(description = "轮播图表单")
public class BlogCarouselForm {

    @NotNull(message = "文件ID不能为空")
    @Schema(description = "轮播图文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long fileId;
}