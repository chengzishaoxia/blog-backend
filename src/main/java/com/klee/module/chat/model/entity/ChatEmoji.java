package com.klee.module.chat.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 聊天表情实体：保存文件ID及尺寸，userId 为空表示全局，逻辑删除标记 isDeleted。
 * 对应表: chat_emoji
 */
@Getter
@Setter
@TableName("chat_emoji")
public class ChatEmoji extends BaseEntity {

    /** 文件记录ID (关联 file_info.id) */
    private Long fileId;
    /** 宽度(像素，可选) */
    private Integer width;
    /** 高度(像素，可选) */
    private Integer height;
    /** 所属用户ID (null 表示全局表情) */
    private Long userId;
    /** 逻辑删除：0 未删除 1 已删除 */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
    /** 图片二进制内容MD5(同一用户或全局内做去重) */
    private String md5;
}
