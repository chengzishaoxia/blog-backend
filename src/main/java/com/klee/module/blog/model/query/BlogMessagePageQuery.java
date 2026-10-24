package com.klee.module.blog.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "留言分页查询")
public class BlogMessagePageQuery extends BasePageQuery {

    @Schema(description = "关键字(留言内容)")
    private String keywords;

    @Schema(description = "审核状态 0-待审核 1-通过 2-驳回")
    private Integer auditStatus;
}
