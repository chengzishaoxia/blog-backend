package com.klee.module.chat.server;

import com.klee.module.chat.server.handler.RealtimeGatewayHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * WebSocket 服务器初始化器：为每个新连接设置 ChannelPipeline。
 */
@RequiredArgsConstructor
@Component
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final RealtimeGatewayHandler realtimeGatewayHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(65536));
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new IdleStateHandler(60, 0, 0));
        p.addLast(realtimeGatewayHandler);
    }
}
