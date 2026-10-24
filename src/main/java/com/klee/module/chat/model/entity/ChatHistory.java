package com.klee.module.chat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 聊天消息历史持久化实体
 *
 * 对应表: chat_history
 * 建议建表SQL:
 * CREATE TABLE chat_history (
 *   id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *   type VARCHAR(20) NOT NULL COMMENT '消息类型',
 *   from_user_id BIGINT COMMENT '发送者用户ID',
 *   from_username VARCHAR(64) COMMENT '发送者用户名',
 *   from_nickname VARCHAR(64) COMMENT '发送者昵称',
 *   from_avatar VARCHAR(255) COMMENT '发送者头像URL',
 *   to_user_id BIGINT COMMENT '接收者用户ID(空=广播)',
 *   content TEXT COMMENT '消息内容',
 *   error VARCHAR(100) COMMENT '错误描述(type=ERROR)',
 *   is_private TINYINT(1) COMMENT '是否私聊 0/1',
 *   send_timestamp BIGINT COMMENT '发送时间戳(ms)',
 *   create_time DATETIME NULL,
 *   update_time DATETIME NULL,
 *   INDEX idx_from_user (from_user_id),
 *   INDEX idx_to_user (to_user_id),
 *   INDEX idx_timestamp (send_timestamp),
 *   INDEX idx_type (type)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
@Getter
@Setter
@TableName("chat_history")
public class ChatHistory extends BaseEntity {

    /** 消息类型 (CHAT / SYSTEM / PING / PONG / JOIN / LEAVE / ERROR) */
    private String type;

    private Long fromUserId;
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;

    private Long toUserId; // null=广播

    private String content;

    private String error;

    /** 0/1 表示是否私聊 */
    @TableField("is_private")
    private Integer isPrivate;

    /** 发送时间戳（毫秒） */
    @TableField("send_timestamp")
    private Long sendTimestamp;
}

