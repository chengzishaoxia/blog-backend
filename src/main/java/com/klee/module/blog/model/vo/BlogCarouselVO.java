package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图展示对象
 */
@Data
@Schema(description = "轮播图VO")
public class BlogCarouselVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

