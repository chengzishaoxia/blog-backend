package com.klee.shared.realtime.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.common.exception.BusinessException;
import com.klee.module.chat.model.ChatMessage;
import com.klee.module.chat.model.MessageType;
import com.klee.module.chat.service.ChatBanStateService;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import com.klee.shared.realtime.model.AdminSocketEnvelope;
import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.shared.realtime.model.RealtimeConnectionDetailVO;
import com.klee.shared.realtime.model.RealtimeConnectionPageQuery;
import com.klee.shared.realtime.model.RealtimeConnectionPageVO;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import com.klee.shared.realtime.model.RealtimeSummaryVO;
import com.klee.shared.realtime.service.RealtimeManagementService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class RealtimeManagementServiceImpl implements RealtimeManagementService {

    private final RealtimeSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;
    private final ChatBanStateService chatBanStateService;

    public RealtimeManagementServiceImpl(RealtimeSessionRegistry sessionRegistry,
                                         ObjectMapper objectMapper,
                                         ChatBanStateService chatBanStateService) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
        this.chatBanStateService = chatBanStateService;
    }

    @Override
    public RealtimeSummaryVO getSummary() {
        RealtimeSummaryVO summaryVO = new RealtimeSummaryVO();
        summaryVO.setOnlineUserCount(sessionRegistry.countUniqueUsers(null));
        summaryVO.setConnectionCount(sessionRegistry.countConnections(null));
        summaryVO.setAdminConnectionCount(sessionRegistry.countConnections(RealtimeChannelType.ADMIN));
        summaryVO.setChatConnectionCount(sessionRegistry.countConnections(RealtimeChannelType.CHAT));
        return summaryVO;
    }

    @Override
    public Page<RealtimeConnectionPageVO> getConnectionPage(RealtimeConnectionPageQuery query) {
        List<RealtimeConnectionPageVO> records = sessionRegistry.getActiveSnapshots().stream()
                .filter(snapshot -> matchKeyword(snapshot, query.getKeyword()))
                .filter(snapshot -> matchChannelType(snapshot, query.getChannelType()))
                .sorted(Comparator.comparingLong(RealtimeConnectionSnapshot::getConnectedAt).reversed())
                .map(this::toPageVO)
                .toList();

        long pageNum = query.getPageNum();
        long pageSize = query.getPageSize();
        int fromIndex = (int) Math.max((pageNum - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + (int) pageSize, records.size());

        Page<RealtimeConnectionPageVO> page = new Page<>(pageNum, pageSize, records.size());
        if (fromIndex >= records.size()) {
            page.setRecords(List.of());
            return page;
        }
        page.setRecords(records.subList(fromIndex, toIndex));
        return page;
    }

    @Override
    public RealtimeConnectionDetailVO getConnectionDetail(String connectionId) {
        RealtimeConnectionSnapshot snapshot = sessionRegistry.getSnapshot(connectionId);
        if (snapshot == null) {
            throw new BusinessException("连接不存在或已断开");
        }
        return toDetailVO(snapshot);
    }

    @Override
    public boolean disconnect(String connectionId, String reason) {
        Channel channel = sessionRegistry.getChannel(connectionId);
        RealtimeConnectionSnapshot snapshot = sessionRegistry.getSnapshot(connectionId);
        if (channel == null || snapshot == null) {
            return false;
        }
        String actualReason = (reason == null || reason.isBlank()) ? "管理员手动断开连接" : reason.trim();
        channel.writeAndFlush(new TextWebSocketFrame(buildKickMessage(snapshot, actualReason)))
                .addListener(ChannelFutureListener.CLOSE);
        return true;
    }

    @Override
    public boolean banChat(String connectionId, String reason) {
        RealtimeConnectionSnapshot snapshot = sessionRegistry.getSnapshot(connectionId);
        if (snapshot == null) {
            return false;
        }
        if (snapshot.getChannelType() != RealtimeChannelType.CHAT) {
            throw new BusinessException("只有聊天室连接支持封禁发言");
        }
        String actualReason = (reason == null || reason.isBlank()) ? "管理员已封禁当前账号的聊天室发言权限" : reason.trim();
        chatBanStateService.banUser(snapshot.getUserId(), actualReason);
        notifyChatUser(snapshot.getUserId(), "你已被管理员封禁发言：" + actualReason);
        return true;
    }

    @Override
    public boolean unbanChat(String connectionId) {
        RealtimeConnectionSnapshot snapshot = sessionRegistry.getSnapshot(connectionId);
        if (snapshot == null) {
            return false;
        }
        if (snapshot.getChannelType() != RealtimeChannelType.CHAT) {
            throw new BusinessException("只有聊天室连接支持解除发言封禁");
        }
        chatBanStateService.unbanUser(snapshot.getUserId());
        notifyChatUser(snapshot.getUserId(), "你的聊天室发言封禁已解除");
        return true;
    }

    private boolean matchKeyword(RealtimeConnectionSnapshot snapshot, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String normalized = keyword.toLowerCase(Locale.ROOT);
        return contains(snapshot.getUsername(), normalized)
                || contains(snapshot.getNickname(), normalized)
                || contains(snapshot.getRemoteAddress(), normalized)
                || contains(snapshot.getPath(), normalized);
    }

    private boolean matchChannelType(RealtimeConnectionSnapshot snapshot, String channelType) {
        if (channelType == null || channelType.isBlank()) {
            return true;
        }
        return snapshot.getChannelType().name().equalsIgnoreCase(channelType);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private RealtimeConnectionPageVO toPageVO(RealtimeConnectionSnapshot snapshot) {
        RealtimeConnectionPageVO pageVO = new RealtimeConnectionPageVO();
        pageVO.setConnectionId(snapshot.getConnectionId());
        pageVO.setUserId(snapshot.getUserId());
        pageVO.setUsername(snapshot.getUsername());
        pageVO.setNickname(snapshot.getNickname());
        pageVO.setAvatar(snapshot.getAvatar());
        pageVO.setChannelType(snapshot.getChannelType().name());
        pageVO.setPath(snapshot.getPath());
        pageVO.setRemoteAddress(snapshot.getRemoteAddress());
        pageVO.setConnectedAt(snapshot.getConnectedAt());
        pageVO.setLastSeenAt(snapshot.getLastSeenAt());
        pageVO.setSubscriptions(snapshot.getSubscriptions());
        fillChatBanState(snapshot, pageVO);
        return pageVO;
    }

    private RealtimeConnectionDetailVO toDetailVO(RealtimeConnectionSnapshot snapshot) {
        RealtimeConnectionDetailVO detailVO = new RealtimeConnectionDetailVO();
        detailVO.setConnectionId(snapshot.getConnectionId());
        detailVO.setUserId(snapshot.getUserId());
        detailVO.setUsername(snapshot.getUsername());
        detailVO.setNickname(snapshot.getNickname());
        detailVO.setAvatar(snapshot.getAvatar());
        detailVO.setChannelType(snapshot.getChannelType().name());
        detailVO.setPath(snapshot.getPath());
        detailVO.setRemoteAddress(snapshot.getRemoteAddress());
        detailVO.setConnectedAt(snapshot.getConnectedAt());
        detailVO.setLastSeenAt(snapshot.getLastSeenAt());
        detailVO.setSubscriptions(snapshot.getSubscriptions());
        fillChatBanState(snapshot, detailVO);
        return detailVO;
    }

    private void fillChatBanState(RealtimeConnectionSnapshot snapshot, RealtimeConnectionPageVO pageVO) {
        boolean chatConnection = snapshot.getChannelType() == RealtimeChannelType.CHAT;
        pageVO.setChatBanned(chatConnection && chatBanStateService.isUserBanned(snapshot.getUserId()));
        pageVO.setChatBanReason(pageVO.isChatBanned() ? chatBanStateService.getBanReason(snapshot.getUserId()) : null);
    }

    private void fillChatBanState(RealtimeConnectionSnapshot snapshot, RealtimeConnectionDetailVO detailVO) {
        boolean chatConnection = snapshot.getChannelType() == RealtimeChannelType.CHAT;
        detailVO.setChatBanned(chatConnection && chatBanStateService.isUserBanned(snapshot.getUserId()));
        detailVO.setChatBanReason(detailVO.isChatBanned() ? chatBanStateService.getBanReason(snapshot.getUserId()) : null);
    }

    private void notifyChatUser(Long userId, String content) {
        ChatMessage message = new ChatMessage();
        message.setType(MessageType.SYSTEM);
        message.setFromUserId(0L);
        message.setFromUsername("system");
        message.setFromNickname("系统");
        message.setToUserId(userId);
        message.setIsPrivate(true);
        message.setContent(content);
        String json = toJson(message);
        for (Channel channel : sessionRegistry.getChannelsByUser(userId, RealtimeChannelType.CHAT)) {
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private String buildKickMessage(RealtimeConnectionSnapshot snapshot, String reason) {
        try {
            if (snapshot.getChannelType() == RealtimeChannelType.ADMIN) {
                return objectMapper.writeValueAsString(AdminSocketEnvelope.builder()
                        .type("FORCE_OFFLINE")
                        .message(reason)
                        .timestamp(System.currentTimeMillis())
                        .build());
            }
            ChatMessage message = new ChatMessage();
            message.setType(MessageType.SYSTEM);
            message.setFromUserId(0L);
            message.setFromUsername("system");
            message.setFromNickname("系统");
            message.setToUserId(snapshot.getUserId());
            message.setIsPrivate(true);
            message.setContent("连接已被管理员断开：" + reason);
            return objectMapper.writeValueAsString(message);
        } catch (Exception ex) {
            return "{\"message\":\"连接已被管理员断开\"}";
        }
    }
}