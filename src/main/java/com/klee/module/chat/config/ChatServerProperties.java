package com.klee.module.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 聊天服务器配置属性。
 * 前缀：chat
 * 可在 application.yml 中追加：
 * chat:
 *   enabled: true        # 是否启用聊天模块
 *   port: 18080          # Netty WebSocket 监听端口（独立于 Spring Boot 端口）
 *   path: /ws/chat       # WebSocket 握手路径
 *   # （已移除 allowMultiSession，现默认允许同一账号多终端同时在线）
 */
@Data
@ConfigurationProperties(prefix = "chat")
public class ChatServerProperties {
    /** 是否启用聊天模块 */
    private boolean enabled = true;
    /** Netty WebSocket 监听端口 */
    private int port = 18080;
    /** WebSocket 握手的路径（浏览器发起 ws://host:port/path?token=xxx） */
    private String path = "/ws/chat";
    /** 后管实时通道路径 */
    private String adminPath = "/ws/admin";
    /** 内存中保存的最大历史消息条数（环形缓存） */
    private int historyCapacity = 1000;
}
