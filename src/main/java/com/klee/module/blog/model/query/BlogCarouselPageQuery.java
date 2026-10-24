package com.klee.module.blog.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 轮播图分页查询
 */
@Data
@Schema(description = "轮播图分页查询")
public class BlogCarouselPageQuery extends BasePageQuery {
    // 预留后续筛选字段
}

