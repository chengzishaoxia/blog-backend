package com.klee.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.system.model.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.system.model.query.LogPageQuery;
import com.klee.system.model.vo.LogPageVO;
import com.klee.system.model.vo.VisitStatsVO;
import com.klee.system.model.vo.VisitTrendVO;

import java.time.LocalDate;

/**
 * 系统日志 服务接口
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
public interface LogService extends IService<Log> {

    /**
     * 获取日志分页列表
     */
    Page<LogPageVO> getLogPage(LogPageQuery queryParams);


    /**
     * 获取访问趋势
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取访问统计
     */
    VisitStatsVO getVisitStats();

}
