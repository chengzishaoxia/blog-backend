package com.klee.module.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogMessage;
import com.klee.module.blog.model.form.BlogMessageAuditForm;
import com.klee.module.blog.model.form.BlogMessageForm;
import com.klee.module.blog.model.query.BlogMessagePageQuery;
import com.klee.module.blog.model.vo.BlogMessageDanmakuVO;
import com.klee.module.blog.model.vo.BlogMessageVO;

import java.util.List;

public interface BlogMessageService extends IService<BlogMessage> {

    IPage<BlogMessageVO> pageMessages(BlogMessagePageQuery query);

    boolean submitMessage(BlogMessageForm form, String submitIp);

    boolean auditMessage(Long id, BlogMessageAuditForm form);

    boolean deleteMessage(Long id);

    List<BlogMessageDanmakuVO> listApprovedDanmaku(Integer limit);
}
