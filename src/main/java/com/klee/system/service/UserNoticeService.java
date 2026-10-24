package com.klee.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.system.model.entity.UserNotice;
import com.klee.system.model.query.NoticePageQuery;
import com.klee.system.model.vo.UserNoticePageVO;
import com.klee.system.model.vo.NoticePageVO;

/**
 * 用户公告状态服务类
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
public interface UserNoticeService extends IService<UserNotice> {

    /**
     * 全部标记为已读
     *
     * @return 是否成功
     */
    boolean readAll();

    /**
     * 分页获取我的通知公告
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return 我的通知公告分页列表
     */
    IPage<UserNoticePageVO> getMyNoticePage(Page<NoticePageVO> page, NoticePageQuery queryParams);
}
