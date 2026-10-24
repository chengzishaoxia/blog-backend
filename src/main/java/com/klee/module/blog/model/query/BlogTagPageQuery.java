package com.klee.module.blog.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 博客标签分页查询
 */
@Getter
@Setter
@Schema(description = "博客标签分页查询")
public class BlogTagPageQuery extends BasePageQuery {

    @Schema(description = "关键字(标签名称)")
    private String keywords;

    @Schema(description = "状态 1-启用 0-停用")
    private Integer status;
}

