package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "博客标签VO")
public class BlogTagVO {
    @Schema(description = "标签ID")
    private Long id;
    @Schema(description = "标签名称")
    private String name;
    @Schema(description = "排序值(越小越前)")
    private Integer sort;
    @Schema(description = "状态 1-启用 0-停用")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "关联博客数量")
    private Long postCount;
}
