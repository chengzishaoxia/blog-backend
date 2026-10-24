package com.klee.module.chat.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.shared.realtime.RealtimeMessagingService;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import com.klee.shared.realtime.model.AdminSocketRequest;
import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminRouteHandler implements SocketRouteHandler {

    private final ObjectMapper objectMapper;
    private final RealtimeSessionRegistry sessionRegistry;
    private final RealtimeMessagingService realtimeMessagingService;

    @Override
    public String path() {
        return "/ws/admin";
    }

    @Override
    public RealtimeChannelType channelType() {
        return RealtimeChannelType.ADMIN;
    }

    @Override
    public void afterConnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot) {
        realtimeMessagingService.publishOnlineCount();
    }

    @Override
    public void handleTextMessage(ChannelHandlerContext ctx, String text) {
        AdminSocketRequest request;
        try {
            request = objectMapper.readValue(text, AdminSocketRequest.class);
        } catch (Exception ex) {
            realtimeMessagingService.sendError(ctx.channel(), "INVALID_JSON");
            return;
        }
        String type = request.getType();
        if (type == null || type.isBlank()) {
            realtimeMessagingService.sendError(ctx.channel(), "INVALID_TYPE");
            return;
        }
        switch (type.toUpperCase()) {
            case "SUBSCRIBE" -> subscribe(ctx, request.getTopic());
            case "UNSUBSCRIBE" -> unsubscribe(ctx, request.getTopic());
            case "PING" -> realtimeMessagingService.sendAck(ctx.channel(), "ping", "pong");
            default -> realtimeMessagingService.sendError(ctx.channel(), "UNSUPPORTED_TYPE");
        }
    }

    @Override
    public void afterDisconnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot) {
        realtimeMessagingService.publishOnlineCount();
    }

    private void subscribe(ChannelHandlerContext ctx, String topic) {
        if (topic == null || topic.isBlank()) {
            realtimeMessagingService.sendError(ctx.channel(), "INVALID_TOPIC");
            return;
        }
        sessionRegistry.subscribe(ctx.channel(), topic, Long.MAX_VALUE / 4);
        if (RealtimeMessagingService.TOPIC_ONLINE_COUNT.equals(topic)) {
            realtimeMessagingService.sendAck(ctx.channel(), topic, new RealtimeMessagingService.OnlineCountPayload(
                    sessionRegistry.countUniqueUsers(null),
                    sessionRegistry.countConnections(null),
                    sessionRegistry.countConnections(RealtimeChannelType.ADMIN),
                    sessionRegistry.countConnections(RealtimeChannelType.CHAT)
            ));
            return;
        }
        if (RealtimeMessagingService.TOPIC_NOTIFICATION.equals(topic)) {
            realtimeMessagingService.sendAck(ctx.channel(), topic, "subscribed");
            return;
        }
        if (RealtimeMessagingService.TOPIC_DICT.equals(topic)) {
            realtimeMessagingService.sendAck(ctx.channel(), topic, "subscribed");
            return;
        }
        realtimeMessagingService.sendError(ctx.channel(), "UNSUPPORTED_TOPIC");
        sessionRegistry.unsubscribe(ctx.channel(), topic);
    }

    private void unsubscribe(ChannelHandlerContext ctx, String topic) {
        if (topic == null || topic.isBlank()) {
            return;
        }
        sessionRegistry.unsubscribe(ctx.channel(), topic);
        realtimeMessagingService.sendAck(ctx.channel(), topic, "unsubscribed");
    }
}