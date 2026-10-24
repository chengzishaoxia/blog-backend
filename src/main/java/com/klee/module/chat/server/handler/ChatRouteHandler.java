package com.klee.module.chat.server.handler;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.module.chat.model.ChatMessage;
import com.klee.module.chat.model.MessageType;
import com.klee.module.chat.service.ChatBanStateService;
import com.klee.module.chat.service.ChatHistoryService;
import com.klee.shared.realtime.RealtimeMessagingService;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import com.klee.system.enums.RoleCodeEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRouteHandler implements SocketRouteHandler {

    private final RealtimeSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;
    private final ChatHistoryService historyService;
    private final ChatBanStateService chatBanStateService;
    private final StpInterface stpInterface;
    private final RealtimeMessagingService realtimeMessagingService;

    @Override
    public String path() {
        return "/ws/chat";
    }

    @Override
    public RealtimeChannelType channelType() {
        return RealtimeChannelType.CHAT;
    }

    @Override
    public void afterConnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot) {
        boolean firstConnection = sessionRegistry.getChannelsByUser(snapshot.getUserId(), RealtimeChannelType.CHAT).size() == 1;
        realtimeMessagingService.publishOnlineCount();
        if (!firstConnection) {
            return;
        }
        ChatMessage join = new ChatMessage();
        join.setType(MessageType.JOIN);
        join.setFromUserId(snapshot.getUserId());
        join.setFromUsername(snapshot.getUsername());
        join.setFromNickname(snapshot.getNickname());
        join.setFromAvatar(snapshot.getAvatar());
        join.setContent("user " + snapshot.getUserId() + " joined");
        join.setIsPrivate(false);
        historyService.add(join);
        broadcast(join, ctx.channel());
    }

    @Override
    public void handleTextMessage(ChannelHandlerContext ctx, String text) {
        RealtimeConnectionSnapshot snapshot = sessionRegistry.getSnapshot(ctx.channel());
        if (snapshot == null) {
            return;
        }
        ChatMessage msg;
        try {
            msg = objectMapper.readValue(text, ChatMessage.class);
        } catch (Exception ex) {
            ChatMessage err = new ChatMessage();
            err.setType(MessageType.ERROR);
            err.setError("INVALID_JSON");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(toJson(err)));
            return;
        }
        msg.setFromUserId(snapshot.getUserId());
        msg.setFromUsername(snapshot.getUsername());
        msg.setFromNickname(snapshot.getNickname());
        msg.setFromAvatar(snapshot.getAvatar());

        if (msg.getType() == MessageType.PING) {
            ChatMessage pong = new ChatMessage();
            pong.setType(MessageType.PONG);
            pong.setFromUserId(0L);
            pong.setFromUsername("system");
            pong.setFromNickname("系统");
            pong.setIsPrivate(true);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(toJson(pong)));
            return;
        }

        if (msg.getType() != MessageType.CHAT && msg.getType() != MessageType.IMAGE) {
            ChatMessage err = new ChatMessage();
            err.setType(MessageType.ERROR);
            err.setError("UNSUPPORTED_TYPE");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(toJson(err)));
            return;
        }

        if (chatBanStateService.isUserBanned(snapshot.getUserId())) {
            ChatMessage deny = new ChatMessage();
            deny.setType(MessageType.SYSTEM);
            deny.setFromUserId(0L);
            deny.setFromUsername("system");
            deny.setFromNickname("系统");
            deny.setToUserId(snapshot.getUserId());
            deny.setIsPrivate(true);
            deny.setContent("你已被管理员封禁发言：" + chatBanStateService.getBanReason(snapshot.getUserId()));
            broadcastTo(deny, sessionRegistry.getChannelsByUser(snapshot.getUserId(), RealtimeChannelType.CHAT));
            return;
        }

        boolean allowed;
        try {
            var perms = stpInterface.getPermissionList(snapshot.getUserId(), StpUtil.getLoginType());
            var roles = stpInterface.getRoleList(snapshot.getUserId(), StpUtil.getLoginType());
            allowed = (CollectionUtil.isNotEmpty(perms) && perms.contains("chat:send"))
                    || (CollectionUtil.isNotEmpty(roles) && roles.contains(RoleCodeEnum.ROOT.getCode()));
        } catch (Exception ex) {
            allowed = false;
        }
        if (!allowed) {
            ChatMessage deny = new ChatMessage();
            deny.setType(MessageType.SYSTEM);
            deny.setFromUserId(0L);
            deny.setFromUsername("系统管理员");
            deny.setFromNickname("可变羽翼核心");
            deny.setIsPrivate(true);
            deny.setToUserId(snapshot.getUserId());
            deny.setContent("只有『可变羽翼核心』才能发消息哦~");
            broadcastTo(deny, sessionRegistry.getChannelsByUser(snapshot.getUserId(), RealtimeChannelType.CHAT));
            return;
        }

        msg.setIsPrivate(msg.getToUserId() != null);
        if (msg.getToUserId() != null) {
            broadcastTo(msg, sessionRegistry.getChannelsByUser(snapshot.getUserId(), RealtimeChannelType.CHAT));
            broadcastTo(msg, sessionRegistry.getChannelsByUser(msg.getToUserId(), RealtimeChannelType.CHAT));
        } else {
            broadcast(msg, null);
        }
        historyService.add(msg);
    }

    @Override
    public void afterDisconnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot) {
        realtimeMessagingService.publishOnlineCount();
        boolean stillOnline = sessionRegistry.isUserOnline(snapshot.getUserId(), RealtimeChannelType.CHAT);
        if (stillOnline) {
            return;
        }
        ChatMessage leave = new ChatMessage();
        leave.setType(MessageType.LEAVE);
        leave.setFromUserId(snapshot.getUserId());
        leave.setFromUsername(snapshot.getUsername());
        leave.setFromNickname(snapshot.getNickname());
        leave.setFromAvatar(snapshot.getAvatar());
        leave.setContent("user " + snapshot.getUserId() + " left");
        leave.setIsPrivate(false);
        historyService.add(leave);
        broadcast(leave, ctx.channel());
    }

    private void broadcast(ChatMessage message, Channel exclude) {
        String json = toJson(message);
        for (Channel channel : sessionRegistry.getChannelsByType(RealtimeChannelType.CHAT)) {
            if (exclude != null && exclude == channel) {
                continue;
            }
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private void broadcastTo(ChatMessage message, Set<Channel> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }
        String json = toJson(message);
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            log.warn("序列化聊天室消息失败 ex={}", ex.getMessage());
            return "{}";
        }
    }
}