package com.klee.module.chat.model.vo;

import lombok.Data;

/**
 * 表情包返回 VO
 */
@Data
public class ChatEmojiVO {
    private Long id;
    private Long fileId; // 文件记录ID
    private String imageUrl; // 兼容前端旧字段：图片访问URL
    private Integer width;
    private Integer height;
    private Long userId;
    private String md5;
}
