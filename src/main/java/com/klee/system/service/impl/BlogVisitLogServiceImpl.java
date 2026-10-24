package com.klee.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.system.mapper.BlogVisitLogMapper;
import com.klee.system.model.entity.BlogVisitLog;

import com.klee.system.model.query.LogPageQuery;
import com.klee.system.model.vo.BlogVisitLogVO;
import com.klee.system.model.vo.LogPageVO;
import com.klee.system.service.BlogVisitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author ljy33
* @description 针对表【blog_visit_log】的数据库操作Service实现
* @createDate 2026-04-20 20:35:30
*/
@Service
@RequiredArgsConstructor
public class BlogVisitLogServiceImpl extends ServiceImpl<BlogVisitLogMapper, BlogVisitLog>
    implements BlogVisitLogService {

    /**
     * 获取日志分页列表
     *
     * @param queryParams 查询参数
     * @return 日志分页列表
     */
    @Override
    public Page<BlogVisitLogVO> getLogPage(LogPageQuery queryParams) {
        return this.baseMapper.getLogPage(new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams);
    }
}




