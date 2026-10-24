package com.klee.module.chat.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.Result;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.chat.service.ChatHistoryService;
import com.klee.module.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天模块辅助 REST 接口（调试/查看状态）。
 * 说明：
 *   - 仅提供简单的在线用户查询与运行状态查看。
 *   - 实际消息收发通过 WebSocket 进行。
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatHistoryService historyService;

    /**
     * 拉取最近聊天历史（含广播 + 与自己相关的私聊）。
     * 示例：GET /api/chat/history?limit=50
     */
    @SaCheckLogin
    @GetMapping("/history")
    @Log(value = "拉取聊天历史", module = LogModuleEnum.CHAT)
    public Result<List<ChatMessage>> history(@RequestParam(required = false, defaultValue = "50") Integer limit) {
        Long uid = SecurityUtils.getUserId();
        List<ChatMessage> list = historyService.listRecent(limit == null ? 50 : limit, uid);
        return Result.success(list);
    }
}
