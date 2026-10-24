package com.klee.shared.file.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件分页展示 VO
 */
@Data
@Schema(description = "文件分页VO")
public class FileInfoPageVO {
    @Schema(description = "文件ID")
    private Long id;
    @Schema(description = "文件名称")
    private String name;
    @Schema(description = "文件访问URL")
    private String url;
    @Schema(description = "用途类型")
    private String usageType;
    @Schema(description = "大小(字节)")
    private Long size;
    @Schema(description = "Content-Type")
    private String contentType;
    @Schema(description = "上传者用户ID")
    private Long uploaderUserId;
    @Schema(description = "上传者用户名")
    private String uploaderUsername; // 新增: 对应用户名
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
