package com.klee.module.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klee.module.chat.mapper.ChatHistoryMapper;
import com.klee.module.chat.model.ChatMessage;
import com.klee.module.chat.model.entity.ChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天历史持久化服务（基于数据库）。
 */
@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryMapper chatHistoryMapper;
    private final int defaultLimit = 50;

    // 保存消息
    public void add(ChatMessage msg) {
        if (msg == null) return;
        ChatHistory h = new ChatHistory();
        h.setType(msg.getType() == null ? null : msg.getType().name());
        h.setFromUserId(msg.getFromUserId());
        h.setFromUsername(msg.getFromUsername());
        h.setFromNickname(msg.getFromNickname());
        h.setFromAvatar(msg.getFromAvatar());
        h.setToUserId(msg.getToUserId());
        h.setContent(msg.getContent());
        h.setError(msg.getError());
        h.setIsPrivate(Boolean.TRUE.equals(msg.getIsPrivate()) ? 1 : 0);
        h.setSendTimestamp(msg.getTimestamp() != null ? msg.getTimestamp() : System.currentTimeMillis());
        chatHistoryMapper.insert(h);
    }

    /**
     * 获取最近 limit 条与指定用户相关的消息（同原语义）。
     * userId == null 仅返回广播消息。
     */
    public List<ChatMessage> listRecent(int limit, Long userId) {
        if (limit <= 0) limit = defaultLimit;
        // 使用 LambdaQueryWrapper 取代字符串列名，提升类型安全
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        if (userId == null) {
            wrapper.isNull(ChatHistory::getToUserId);
        } else {
            wrapper.and(w -> w.isNull(ChatHistory::getToUserId)
                    .or().eq(ChatHistory::getFromUserId, userId)
                    .or().eq(ChatHistory::getToUserId, userId));
        }
        wrapper.orderByDesc(ChatHistory::getId).last("limit " + limit);
        List<ChatHistory> list = chatHistoryMapper.selectList(wrapper);
        Collections.reverse(list); // 正序返回
        return list.stream().map(this::toMessage).collect(Collectors.toList());
    }

    private ChatMessage toMessage(ChatHistory h) {
        ChatMessage m = new ChatMessage();
        m.setType(h.getType() == null ? null : com.klee.module.chat.model.MessageType.valueOf(h.getType()));
        m.setFromUserId(h.getFromUserId());
        m.setFromUsername(h.getFromUsername());
        m.setFromNickname(h.getFromNickname());
        m.setFromAvatar(h.getFromAvatar());
        m.setToUserId(h.getToUserId());
        m.setContent(h.getContent());
        m.setError(h.getError());
        m.setIsPrivate(h.getIsPrivate() != null && h.getIsPrivate() == 1);
        m.setTimestamp(h.getSendTimestamp());
        return m;
    }
}
