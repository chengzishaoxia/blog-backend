package com.klee.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.system.model.entity.BlogVisitLog;
import com.klee.system.model.query.LogPageQuery;
import com.klee.system.model.vo.BlogVisitLogVO;
import com.klee.system.model.vo.LogPageVO;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ljy33
* @description 针对表【blog_visit_log】的数据库操作Mapper
* @createDate 2026-04-20 20:35:30
* @Entity generator.domain.BlogVisitLog
*/
@Mapper
public interface BlogVisitLogMapper extends BaseMapper<BlogVisitLog> {
    /**
     * 获取日志分页列表
     */
    Page<BlogVisitLogVO> getLogPage(Page<LogPageVO> page, LogPageQuery queryParams);
}




