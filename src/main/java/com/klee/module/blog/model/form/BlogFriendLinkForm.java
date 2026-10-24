package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "博客友链表单")
public class BlogFriendLinkForm implements Serializable {

    private Long id;

    @NotBlank
    @Schema(description = "站点名称")
    private String name;

    @NotBlank
    @Schema(description = "站点地址")
    private String url;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "站点描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 1-启用 0-停用")
    private Integer status;
}
