package com.klee.module.blog.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "博客友链分页查询")
public class BlogFriendLinkPageQuery extends BasePageQuery {

    @Schema(description = "关键字(站点名称/描述)")
    private String keywords;

    @Schema(description = "状态 1-启用 0-停用")
    private Integer status;
}
