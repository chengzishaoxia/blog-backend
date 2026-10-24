package com.klee.module.chat.service.impl;

import com.klee.module.chat.service.ChatBanStateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatBanStateServiceImpl implements ChatBanStateService {

    private static final String CHAT_BAN_KEY_PREFIX = "chat:speak:ban:";

    private final RedisTemplate<String, Object> redisTemplate;

    public ChatBanStateServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isUserBanned(Long userId) {
        if (userId == null) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(userId)));
    }

    @Override
    public String getBanReason(Long userId) {
        if (userId == null) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(buildKey(userId));
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public void banUser(Long userId, String reason) {
        if (userId == null) {
            return;
        }
        redisTemplate.opsForValue().set(buildKey(userId), normalizeReason(reason));
    }

    @Override
    public void unbanUser(Long userId) {
        if (userId == null) {
            return;
        }
        redisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return CHAT_BAN_KEY_PREFIX + userId;
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "管理员已封禁当前账号的聊天室发言权限";
        }
        return reason.trim();
    }
}