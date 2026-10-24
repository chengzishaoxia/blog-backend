package com.klee.module.chat.server;

import com.klee.module.chat.config.ChatServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty 聊天服务器入口：随 Spring Boot 启动并监听独立端口。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatServer implements ApplicationRunner, AutoCloseable {

    private final ChatServerProperties properties; // 配置项（端口、路径、开关）
    private final WebSocketServerInitializer webSocketServerInitializer; // 新增：注入 initializer

    private EventLoopGroup bossGroup;    // 接收连接
    private EventLoopGroup workerGroup;  // 处理读写
    private Channel serverChannel;       // 监听 Channel
    private final AtomicBoolean started = new AtomicBoolean(false); // 防重复启动

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Chat server disabled");
            return;
        }
        start();
    }

    /** 启动 Netty WebSocket 服务器 */
    public void start() {
        if (started.get()) return;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(webSocketServerInitializer); // 使用 Spring 注入的 initializer
            ChannelFuture f = b.bind(properties.getPort()).sync();
            serverChannel = f.channel();
            started.set(true);
            log.info("Chat server started on port {} path {}", properties.getPort(), properties.getPath());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to start chat server", e);
            stop();
        }
    }

    /** 停止服务器并释放资源 */
    public void stop() {
        if (!started.get()) return;
        try {
            if (serverChannel != null) {
                serverChannel.close().syncUninterruptibly();
            }
        } finally {
            if (bossGroup != null) bossGroup.shutdownGracefully();
            if (workerGroup != null) workerGroup.shutdownGracefully();
            started.set(false);
            log.info("Chat server stopped");
        }
    }


    @Override
    public void close() { // Spring 关闭时自动调用
        stop();
    }
}
