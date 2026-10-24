package com.klee.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.system.model.query.LogPageQuery;
import com.klee.system.model.vo.BlogVisitLogVO;
import com.klee.system.model.vo.LogPageVO;
import com.klee.system.model.vo.VisitStatsVO;
import com.klee.system.model.vo.VisitTrendVO;
import com.klee.system.service.BlogVisitLogService;
import com.klee.system.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 日志控制层
 *
 * @author Ray.Hao
 * @since 2.10.0
 */
@Tag(name = "101.访问ip日志接口")
@RestController
@RequestMapping("/api/v1/visit-logs")
@RequiredArgsConstructor
public class VisitLogsController {

    private final BlogVisitLogService logIpService;

    @Operation(summary = "日志分页列表")
    @GetMapping("/page")
    public PageResult<BlogVisitLogVO> getLogPage(
             LogPageQuery queryParams
    ) {
        Page<BlogVisitLogVO> result = logIpService.getLogPage(queryParams);
        return PageResult.success(result);
    }
/**
    @Operation(summary = "获取访问趋势")
    @GetMapping("/visit-trend")
    public Result<VisitTrendVO> getVisitTrend(
            @Parameter(description = "开始时间", example = "yyyy-MM-dd") @RequestParam String startDate,
            @Parameter(description = "结束时间", example = "yyyy-MM-dd") @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        VisitTrendVO data = logService.getVisitTrend(start, end);
        return Result.success(data);
    }

    @Operation(summary = "获取访问统计")
    @GetMapping("/visit-stats")
    public Result<VisitStatsVO> getVisitStats() {
        VisitStatsVO result = logService.getVisitStats();
        return Result.success(result);
    }

 */
    

}
