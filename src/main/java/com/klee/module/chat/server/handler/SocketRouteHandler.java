package com.klee.module.chat.server.handler;

import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import io.netty.channel.ChannelHandlerContext;

public interface SocketRouteHandler {

    String path();

    RealtimeChannelType channelType();

    void afterConnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot);

    void handleTextMessage(ChannelHandlerContext ctx, String text);

    void afterDisconnect(ChannelHandlerContext ctx, RealtimeConnectionSnapshot snapshot);
}