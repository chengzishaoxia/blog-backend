package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "博客友链VO")
public class BlogFriendLinkVO {

    @Schema(description = "友链ID")
    private Long id;

    @Schema(description = "站点名称")
    private String name;

    @Schema(description = "站点链接")
    private String url;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "简介")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
