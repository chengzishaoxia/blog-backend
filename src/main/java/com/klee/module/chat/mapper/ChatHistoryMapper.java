package com.klee.module.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.module.chat.model.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天历史 Mapper
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
