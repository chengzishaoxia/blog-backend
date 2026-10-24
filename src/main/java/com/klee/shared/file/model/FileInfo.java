package com.klee.shared.file.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 文件信息持久化实体
 *
 * 将原来的简单 FileInfo DTO 改造成数据库实体，新增使用类型、大小、内容类型、上传者等字段，便于后续统一管理。
 */
@Schema(description = "文件对象")
@TableName("file_info")
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfo extends BaseEntity {

    @Schema(description = "原始文件名称")
    private String name;

    @Schema(description = "文件访问URL")
    private String url;

    @Schema(description = "文件用途类型，如: AVATAR, EMOJI, BLOG_COVER, CAROUSEL")
    private String usageType;

    @Schema(description = "文件大小(字节)")
    private Long size;

    @Schema(description = "内容类型(Content-Type)")
    private String contentType;

    @Schema(description = "上传者用户ID")
    private Long uploaderUserId;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Integer isDeleted;
}
