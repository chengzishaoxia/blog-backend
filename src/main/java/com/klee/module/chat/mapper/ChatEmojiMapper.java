package com.klee.module.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.module.chat.model.entity.ChatEmoji;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表情包 Mapper
 */
@Mapper
public interface ChatEmojiMapper extends BaseMapper<ChatEmoji> {
}

