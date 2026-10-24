package com.klee.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 访问趋势统计视图对象
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Data
@Schema(description = "访问趋势统计")
public class VisitTrendVO {

    @Schema(description = "日期列表")
    private List<String> dates;

    @Schema(description = "浏览量(PV)")
    private List<Integer> pvList;

    @Schema(description = "IP数")
    private List<Integer> ipList;

}
