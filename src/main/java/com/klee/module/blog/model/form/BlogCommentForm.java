package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "博客评论表单")
public class BlogCommentForm implements Serializable {

    private Long id;

    @NotNull
    private Long postId;

    private Long parentId; // 可为空

    @NotBlank
    private String content;
}


