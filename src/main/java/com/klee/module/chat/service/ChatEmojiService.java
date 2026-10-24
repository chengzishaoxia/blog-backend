package com.klee.module.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.chat.model.entity.ChatEmoji;
import com.klee.module.chat.model.vo.ChatEmojiVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 表情包服务
 */
public interface ChatEmojiService extends IService<ChatEmoji> {

    /** 新增个人表情，返回ID */
    Long addPersonalEmoji(Long userId, MultipartFile file);


    /** 删除个人表情（逻辑删除） */
    void deletePersonalEmoji(Long userId, Long emojiId);

}
