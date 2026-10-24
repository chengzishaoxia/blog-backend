package com.klee.shared.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.shared.realtime.model.AdminSocketEnvelope;
import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.system.model.event.DictEvent;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeMessagingService {

    public static final String TOPIC_ONLINE_COUNT = "online-count";
    public static final String TOPIC_DICT = "dict";
    public static final String TOPIC_NOTIFICATION = "notification";

    private final RealtimeSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public void publishOnlineCount() {
        publishToTopic(TOPIC_ONLINE_COUNT, new OnlineCountPayload(
                sessionRegistry.countUniqueUsers(null),
                sessionRegistry.countConnections(null),
                sessionRegistry.countConnections(RealtimeChannelType.ADMIN),
                sessionRegistry.countConnections(RealtimeChannelType.CHAT)
        ));
    }

    public void broadcastDictChange(String dictCode) {
        publishToTopic(TOPIC_DICT, new DictEvent(dictCode));
    }

    public void sendNotification(String username, Object payload) {
        Set<Channel> channels = sessionRegistry.findChannels(snapshot ->
                snapshot.getChannelType() == RealtimeChannelType.ADMIN && username.equals(snapshot.getUsername()));
        if (channels.isEmpty()) {
            return;
        }
        AdminSocketEnvelope envelope = AdminSocketEnvelope.builder()
                .type("EVENT")
                .topic(TOPIC_NOTIFICATION)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
        write(channels, envelope);
    }

    public void publishToTopic(String topic, Object payload) {
        AdminSocketEnvelope envelope = AdminSocketEnvelope.builder()
                .type("EVENT")
                .topic(topic)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
        Set<Channel> channels = sessionRegistry.getChannelsByTopic(topic);
        if (channels.isEmpty()) {
            return;
        }
        write(channels, envelope);
    }

    public void sendError(Channel channel, String message) {
        AdminSocketEnvelope envelope = AdminSocketEnvelope.builder()
                .type("ERROR")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        write(channel, envelope);
    }

    public void sendAck(Channel channel, String topic, Object payload) {
        AdminSocketEnvelope envelope = AdminSocketEnvelope.builder()
                .type("ACK")
                .topic(topic)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
        write(channel, envelope);
    }

    private void write(Set<Channel> channels, Object payload) {
        String json = toJson(payload);
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private void write(Channel channel, Object payload) {
        channel.writeAndFlush(new TextWebSocketFrame(toJson(payload)));
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            log.warn("序列化 WebSocket 消息失败 ex={}", ex.getMessage());
            return "{}";
        }
    }

    public record OnlineCountPayload(int onlineUserCount, int connectionCount, int adminConnectionCount,
                                     int chatConnectionCount) {
    }
}