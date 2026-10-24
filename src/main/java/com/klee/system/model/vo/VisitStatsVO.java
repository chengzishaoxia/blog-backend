package com.klee.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 访问统计数据视图对象
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Data
@Schema(description = "访问统计数据")
public class VisitStatsVO {

    @Schema(description = "今日独立访客数 (UV)")
    private Integer todayUvCount;

    @Schema(description = "累计独立访客数 (UV)")
    private Integer totalUvCount;

    @Schema(description = "独立访客增长率")
    private BigDecimal uvGrowthRate;

    @Schema(description = "今日页面浏览量 (PV)")
    private Integer todayPvCount;

    @Schema(description = "累计页面浏览量 (PV)")
    private Integer totalPvCount;

    @Schema(description = "页面浏览量增长率")
    private BigDecimal pvGrowthRate;

}
