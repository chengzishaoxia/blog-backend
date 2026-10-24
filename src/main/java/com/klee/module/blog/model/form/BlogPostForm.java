package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "博客文章表单")
public class BlogPostForm implements Serializable {

    private Long id; // 编辑时使用

    @NotBlank
    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @NotBlank
    @Schema(description = "内容")
    private String content;

    @Schema(description = "封面图文件ID")
    private Long coverImageFileId; // 重命名: 原 coverImage -> coverImageFileId

    @Schema(description = "置顶 0-否 1-是")
    private Integer pinned; // 新增字段

    @Schema(description = "推荐 0-否 1-是")
    private Integer isRecommend;

    @NotNull
    @Schema(description = "状态 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "标签ID集合")
    private List<Long> tagIds;
}
