package com.klee.module.blog.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.common.util.IPUtils;
import com.klee.module.blog.model.form.BlogMessageAuditForm;
import com.klee.module.blog.model.form.BlogMessageForm;
import com.klee.module.blog.model.query.BlogMessagePageQuery;
import com.klee.module.blog.model.vo.BlogMessageDanmakuVO;
import com.klee.module.blog.model.vo.BlogMessageVO;
import com.klee.module.blog.service.BlogMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客留言")
@RequestMapping("/api/v1/blog/messages")
public class BlogMessageController {

    private final BlogMessageService messageService;

    @Operation(summary = "留言分页列表")
    @PostMapping("/page")
    @SaCheckPermission("blog:message:query")
    @Log(value = "留言分页列表", module = LogModuleEnum.BLOG)
    public PageResult<BlogMessageVO> page(@RequestBody BlogMessagePageQuery query) {
        IPage<BlogMessageVO> page = messageService.pageMessages(query);
        return PageResult.success(page);
    }

    @Operation(summary = "提交留言")
    @PostMapping
    @SaIgnore
    @Log(value = "提交留言", module = LogModuleEnum.BLOG)
    public Result<?> submit(@Valid @RequestBody BlogMessageForm form, HttpServletRequest request) {
        return Result.judge(messageService.submitMessage(form, IPUtils.getIpAddr(request)));
    }

    @Operation(summary = "审核留言")
    @PatchMapping("/{id}/audit")
    @SaCheckPermission("blog:message:audit")
    @Log(value = "审核留言", module = LogModuleEnum.BLOG)
    public Result<?> audit(
            @Parameter(description = "留言ID") @PathVariable Long id,
            @Valid @RequestBody BlogMessageAuditForm form
    ) {
        return Result.judge(messageService.auditMessage(id, form));
    }

    @Operation(summary = "删除留言")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:message:delete")
    @Log(value = "删除留言", module = LogModuleEnum.BLOG)
    public Result<?> delete(@PathVariable Long id) {
        return Result.judge(messageService.deleteMessage(id));
    }

    @Operation(summary = "前台: 获取通过审核的弹幕留言")
    @GetMapping("/danmaku")
    @SaIgnore
    @Log(value = "获取弹幕留言", module = LogModuleEnum.BLOG)
    public Result<List<BlogMessageDanmakuVO>> danmaku(@RequestParam(required = false) Integer limit) {
        return Result.success(messageService.listApprovedDanmaku(limit));
    }
}
