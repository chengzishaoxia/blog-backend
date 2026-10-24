package com.klee.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.system.model.entity.BlogVisitLog;
import com.klee.system.model.query.LogPageQuery;
import com.klee.system.model.vo.BlogVisitLogVO;

/**
* @author ljy33
* @description 针对表【blog_visit_log】的数据库操作Service
* @createDate 2026-04-20 20:35:30
*/
public interface BlogVisitLogService extends IService<BlogVisitLog> {
    /**
     * 获取日志分页列表
     */
    Page<BlogVisitLogVO> getLogPage(LogPageQuery queryParams);
}
