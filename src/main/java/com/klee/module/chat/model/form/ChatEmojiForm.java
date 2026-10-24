package com.klee.module.chat.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 表情条目表单：仅上传文件 */
@Data
public class ChatEmojiForm {
    @Schema(description = "表情图片文件", format = "binary")
    private MultipartFile file; // 必填（新增）/ 可选（更新）
}
