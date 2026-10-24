package com.klee.shared.file.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件下载记录表单
 */
@Data
@Schema(description = "文件下载记录表单")
public class FileDownloadForm {

    @Schema(description = "文件名称")
    private String fileName;
}

