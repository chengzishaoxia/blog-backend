package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "博客标签表单")
public class BlogTagForm implements Serializable {

    private Long id;

    @NotBlank
    private String name;

    private Integer sort;

    private Integer status; // 1 启用 0 停用
}

