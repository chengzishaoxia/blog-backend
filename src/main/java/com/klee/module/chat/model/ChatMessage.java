package com.klee.module.chat.model;

import lombok.Data;

import java.time.Instant;

/**
 * WebSocket 传输的聊天消息模型。
 * 前端与后端都使用 JSON 结构互通，示例：
 * {
 *   "type": "CHAT",            // 或 IMAGE、PING 等
 *   "toUserId": 1002,            // 为空表示广播
 *   "content": "你好"            // IMAGE 时为图片 URL
 * }
 * 服务端补充: fromUserId、timestamp；错误时返回 type=ERROR, error="..."。
 *
 * 新增：IMAGE （图片消息）类型，content 字段保存图片的可访问 URL（或对象存储直链）。
 *      发送图片建议流程：1) 通过已有文件上传接口获取 URL；2) 通过 WebSocket 发送 {type: 'IMAGE', content: 'xxx'}。
 *
 * 为了前端直接展示头像/昵称，本模型携带发送者的用户名、昵称、头像。
 */
@Data
public class ChatMessage {
    /** 消息类型：CHAT / IMAGE / SYSTEM / PING / PONG / JOIN / LEAVE / ERROR */
    private MessageType type;
    /** 发送者用户 ID（服务端填充） */
    private Long fromUserId;
    /** 发送者用户名（服务端填充） */
    private String fromUsername;
    /** 发送者昵称（服务端填充） */
    private String fromNickname;
    /** 发送者头像 URL（服务端填充） */
    private String fromAvatar;
    /** 目标用户 ID（为空表示全员广播） */
    private Long toUserId;
    /** 文本内容或图片 URL（PING/PONG 可忽略） */
    private String content;
    /** 服务器时间戳（毫秒） */
    private Long timestamp = Instant.now().toEpochMilli();
    /** 错误描述（type = ERROR 时有效） */
    private String error;
    /** 是否私聊（服务端填充，前端可用于区分显示） */
    private Boolean isPrivate;
}
