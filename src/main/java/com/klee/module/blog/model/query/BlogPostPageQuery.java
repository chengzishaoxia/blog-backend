package com.klee.module.blog.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "博客文章分页查询")
public class BlogPostPageQuery extends BasePageQuery {

    @Schema(description = "关键字(标题)")
    private String keywords;

    @Schema(description = "状态 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "标签ID 过滤")
    private Long tagId;
}
