package com.klee.module.chat.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.klee.common.result.Result;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.chat.model.entity.ChatEmoji;
import com.klee.module.chat.model.form.ChatEmojiForm;
import com.klee.module.chat.model.vo.ChatEmojiVO;
import com.klee.module.chat.service.ChatEmojiService;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.system.enums.RoleCodeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表情包管理 & 公共查询
 */
@RestController
@RequestMapping("/api/chat/emoji")
@RequiredArgsConstructor
public class ChatEmojiController {

    private final ChatEmojiService emojiService;
    @Resource
    private FileInfoMapper fileInfoMapper; // 注入文件信息Mapper

    /**
     * 当前用户自定义表情列表（需登录）
     */
    @SaCheckLogin
    @GetMapping("/mine")
    public Result<List<ChatEmojiVO>> mine() {
        Long uid = SecurityUtils.getUserId();
        if (uid == null) return Result.failed("NOT_LOGIN");
        List<ChatEmoji> entities = emojiService.lambdaQuery()
                .eq(ChatEmoji::getUserId, uid)
                .eq(ChatEmoji::getIsDeleted, 0)
                .orderByDesc(ChatEmoji::getCreateTime)
                .list();
        // 收集文件ID
        List<Long> fileIds = entities.stream().map(ChatEmoji::getFileId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> fileUrlMap = fileIds.isEmpty() ? Map.of() : fileInfoMapper.selectBatchIds(fileIds)
                .stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
        List<ChatEmojiVO> list = entities.stream().map(e -> {
            ChatEmojiVO vo = new ChatEmojiVO();
            BeanUtils.copyProperties(e, vo);
            vo.setImageUrl(fileUrlMap.get(e.getFileId())); // 赋值 URL
            return vo;
        }).toList();
        return Result.success(list);
    }

    /**
     * 用户添加自己的表情（登录，multipart）
     */
    @SaCheckLogin
    @PostMapping(value = "/mine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("chat:emoji:mine:add")
    public Result<Long> addMine(@Valid @ModelAttribute ChatEmojiForm form) {
        Long uid = SecurityUtils.getUserId();
        if (uid == null) return Result.failed("NOT_LOGIN");
        Long id = emojiService.addPersonalEmoji(uid, form.getFile());
        return Result.success(id);
    }

    /**
     * 删除自己的表情（登录即可）
     */
    @SaCheckLogin
    @DeleteMapping("/mine/{id}")
    @SaCheckPermission("chat:emoji:mine:delete")
    public Result<Void> deleteMine(@PathVariable Long id) {
        Long uid = SecurityUtils.getUserId();
        if (uid == null) return Result.failed("NOT_LOGIN");
        emojiService.deletePersonalEmoji(uid, id);
        return Result.success();
    }

}
