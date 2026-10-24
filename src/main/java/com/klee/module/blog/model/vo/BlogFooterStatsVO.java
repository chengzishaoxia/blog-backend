package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "博客页脚统计")
public class BlogFooterStatsVO {

    @Schema(description = "累计页面浏览量(PV)")
    private Integer totalPvCount;
}