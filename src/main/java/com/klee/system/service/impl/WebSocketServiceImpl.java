package com.klee.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.shared.realtime.RealtimeMessagingService;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.system.model.event.DictEvent;
import com.klee.system.service.WebSocketService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WebSocket服务实现类
 * 统一处理WebSocket消息发送和用户在线状态管理
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private final ObjectMapper objectMapper;
    private final RealtimeMessagingService realtimeMessagingService;
    private final RealtimeSessionRegistry sessionRegistry;

    public WebSocketServiceImpl(ObjectMapper objectMapper,
                                RealtimeMessagingService realtimeMessagingService,
                                RealtimeSessionRegistry sessionRegistry) {
        this.objectMapper = objectMapper;
        this.realtimeMessagingService = realtimeMessagingService;
        this.sessionRegistry = sessionRegistry;
    }

    //==================================
    //      用户在线状态管理功能
    //==================================

    /**
     * 用户上线
     *
     * @param username  用户名
     * @param sessionId WebSocket会话ID（可选）
     */
    @Override
    public void userConnected(String username, String sessionId) {
        realtimeMessagingService.publishOnlineCount();
    }

    /**
     * 用户下线
     *
     * @param username 用户名
     */
    @Override
    public void userDisconnected(String username) {
        realtimeMessagingService.publishOnlineCount();
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户名列表
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return sessionRegistry.getActiveSnapshots().stream()
            .map(info -> new UserOnlineDTO(info.getUsername(), info.getConnectedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    public int getOnlineUserCount() {
        return sessionRegistry.countUniqueUsers(null);
    }

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return 是否在线
     */
    public boolean isUserOnline(String username) {
        return sessionRegistry.getActiveSnapshots().stream().anyMatch(item -> username.equals(item.getUsername()));
    }

    /**
     * 手动触发在线用户变更通知
     * 供外部手动触发通知使用
     */
    public void notifyOnlineUsersChange() {
        realtimeMessagingService.publishOnlineCount();
    }

    /**
     * 发送在线用户数量（简化版，不包含用户详情）
     */
    private void sendOnlineUserCount() {
        realtimeMessagingService.publishOnlineCount();
    }

    /**
     * 内部通用通知方法
     * 通知所有客户端在线用户变更
     */
    private void notifyOnlineUsersChangeInternal() {
        sendOnlineUserCount();
    }

    /**
     * 用户在线信息
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * 用户在线DTO（用于返回给前端）
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * 在线用户变更事件
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }

    //==================================
    //      WebSocket消息发送功能
    //==================================

    /**
     * 向所有客户端发送字典更新事件
     *
     * @param dictCode 字典编码
     */
    @Override
    public void broadcastDictChange(String dictCode) {
        DictEvent event = new DictEvent(dictCode);
        sendDictEvent(event);
    }

    /**
     * 发送字典事件消息
     *
     * @param event 字典事件
     */
    private void sendDictEvent(DictEvent event) {
        try {
            objectMapper.writeValueAsString(event);
            realtimeMessagingService.broadcastDictChange(event.getDictCode());
            log.info("已发送字典事件通知, dictCode: {}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("发送字典事件失败", e);
        }
    }

    /**
     * 向特定用户发送系统消息
     *
     * @param username 用户名
     * @param message  消息内容
     */
    @Override
    public void sendNotification(String username, Object message) {
        try {
            objectMapper.writeValueAsString(message);
            realtimeMessagingService.sendNotification(username, message);
            log.info("已向用户[{}]发送消息", username);
        } catch (JsonProcessingException e) {
            log.error("向用户[{}]发送消息失败", username, e);
        }
    }

    /**
     * 发送广播消息给所有用户
     *
     * @param message 消息内容
     */
    public void broadcastMessage(String message) {
        SystemMessage systemMessage = new SystemMessage("系统", message, System.currentTimeMillis());
        realtimeMessagingService.publishToTopic("public", systemMessage);
        log.info("已发送广播消息: {}", message);
    }

    /**
     * 系统消息对象
     */
    @Data
    public static class SystemMessage {
        private String sender;
        private String content;
        private long timestamp;

        public SystemMessage(String sender, String content, long timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}
