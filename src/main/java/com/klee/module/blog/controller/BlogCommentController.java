package com.klee.module.blog.controller;

import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.Result;
import com.klee.module.blog.model.form.BlogCommentForm;
import com.klee.module.blog.model.vo.BlogCommentVO;
import com.klee.module.blog.model.vo.BlogRecentCommentVO;
import com.klee.module.blog.service.BlogCommentService;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客评论")
@RequestMapping("/api/v1/blog/comments")
public class BlogCommentController {

    private final BlogCommentService commentService;

    @Operation(summary = "获取文章评论树")
    @GetMapping("/post/{postId}")
    @SaIgnore
    public Result<List<BlogCommentVO>> listByPost(@PathVariable Long postId) {
        return Result.success(commentService.listCommentsByPost(postId));
    }

    @Operation(summary = "获取最新评论")
    @GetMapping("/recent")
    @SaIgnore
    public Result<List<BlogRecentCommentVO>> listRecent(@RequestParam(defaultValue = "6") Integer limit) {
        int safeLimit = limit == null ? 6 : Math.max(1, Math.min(limit, 12));
        return Result.success(commentService.listRecentComments(safeLimit));
    }

    @Operation(summary = "新增评论")
    @PostMapping
    @Log(value = "新增评论", module = LogModuleEnum.BLOG)
    @SaCheckPermission("blog:comment:add")
    public Result<?> save(@Valid @RequestBody BlogCommentForm form) {
        return Result.judge(commentService.saveComment(form));
    }

    @Operation(summary = "修改评论")
    @PutMapping("/{id}")
    @Log(value = "修改评论", module = LogModuleEnum.BLOG)
    @SaCheckPermission("blog:comment:edit")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody BlogCommentForm form) {
        return Result.judge(commentService.updateComment(id, form));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:comment:delete")
    @Log(value = "删除评论", module = LogModuleEnum.BLOG)
    public Result<?> delete(@PathVariable Long id) {
        return Result.judge(commentService.deleteComment(id));
    }
}


