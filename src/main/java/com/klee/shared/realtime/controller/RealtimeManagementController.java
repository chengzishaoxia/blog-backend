package com.klee.shared.realtime.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.shared.realtime.model.RealtimeConnectionDetailVO;
import com.klee.shared.realtime.model.RealtimeConnectionPageQuery;
import com.klee.shared.realtime.model.RealtimeConnectionPageVO;
import com.klee.shared.realtime.model.RealtimeDisconnectRequest;
import com.klee.shared.realtime.model.RealtimeSummaryVO;
import com.klee.shared.realtime.service.RealtimeManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "实时连接管理")
@RestController
@RequestMapping("/api/v1/realtime/connections")
@RequiredArgsConstructor
public class RealtimeManagementController {

    private final RealtimeManagementService realtimeManagementService;

    @Operation(summary = "获取实时连接概览")
    @GetMapping("/summary")
    @SaCheckPermission("sys:ws:query")
    public Result<RealtimeSummaryVO> getSummary() {
        return Result.success(realtimeManagementService.getSummary());
    }

    @Operation(summary = "获取实时连接分页")
    @GetMapping("/page")
    @SaCheckPermission("sys:ws:query")
    public PageResult<RealtimeConnectionPageVO> getPage(RealtimeConnectionPageQuery query) {
        Page<RealtimeConnectionPageVO> page = realtimeManagementService.getConnectionPage(query);
        return PageResult.success(page);
    }

    @Operation(summary = "获取实时连接详情")
    @GetMapping("/{connectionId}")
    @SaCheckPermission("sys:ws:query")
    public Result<RealtimeConnectionDetailVO> getDetail(@PathVariable String connectionId) {
        return Result.success(realtimeManagementService.getConnectionDetail(connectionId));
    }

    @Operation(summary = "断开实时连接")
    @PostMapping("/{connectionId}/disconnect")
    @SaCheckPermission("sys:ws:disconnect")
    public Result<Boolean> disconnect(@PathVariable String connectionId,
                                      @Valid @RequestBody(required = false) RealtimeDisconnectRequest request) {
        return Result.success(realtimeManagementService.disconnect(connectionId, request == null ? null : request.getReason()));
    }

    @Operation(summary = "封禁聊天室发言")
    @PostMapping("/{connectionId}/chat-ban")
    @SaCheckPermission("sys:chat:ban")
    public Result<Boolean> banChat(@PathVariable String connectionId,
                                   @Valid @RequestBody(required = false) RealtimeDisconnectRequest request) {
        return Result.success(realtimeManagementService.banChat(connectionId, request == null ? null : request.getReason()));
    }

    @Operation(summary = "解除聊天室发言封禁")
    @org.springframework.web.bind.annotation.DeleteMapping("/{connectionId}/chat-ban")
    @SaCheckPermission("sys:chat:ban")
    public Result<Boolean> unbanChat(@PathVariable String connectionId) {
        return Result.success(realtimeManagementService.unbanChat(connectionId));
    }
}