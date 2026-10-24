package com.klee.system.service;

import com.klee.shared.realtime.RealtimeMessagingService;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户在线状态服务
 * 负责维护用户的在线状态和相关统计
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Service
@Slf4j
public class UserOnlineService {

    private final RealtimeSessionRegistry sessionRegistry;
    private final RealtimeMessagingService realtimeMessagingService;

    public UserOnlineService(RealtimeSessionRegistry sessionRegistry,
                             RealtimeMessagingService realtimeMessagingService) {
        this.sessionRegistry = sessionRegistry;
        this.realtimeMessagingService = realtimeMessagingService;
    }

    /**
     * 用户上线
     *
     * @param username  用户名
     * @param sessionId WebSocket会话ID（可选）
     */
    public void userConnected(String username, String sessionId) {
        realtimeMessagingService.publishOnlineCount();
    }

    /**
     * 用户下线
     *
     * @param username 用户名
     */
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
     * 通知所有客户端在线用户变更
     */
    private void notifyOnlineUsersChange() {
        sendOnlineUserCount();
    }

    /**
     * 发送在线用户数量（简化版，不包含用户详情）
     */
    private void sendOnlineUserCount() {
        realtimeMessagingService.publishOnlineCount();
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
}
