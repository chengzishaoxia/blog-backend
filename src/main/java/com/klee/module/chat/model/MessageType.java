package com.klee.module.chat.model;

/**
 * 消息类型枚举：
 *  PING  - 客户端心跳请求
 *  PONG  - 服务端心跳响应
 *  SYSTEM- 预留系统通知类型
 *  CHAT  - 普通聊天消息（文本）
 *  IMAGE - 图片消息（content 保存图片 URL 或资源标识）
 *  JOIN  - 用户加入广播
 *  LEAVE - 用户离开广播
 *  ERROR - 服务端错误反馈
 */
public enum MessageType {
    PING, PONG, SYSTEM, CHAT, IMAGE, JOIN, LEAVE, ERROR
}
