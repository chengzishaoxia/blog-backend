package com.klee.module.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.module.blog.model.form.BlogFriendLinkForm;
import com.klee.module.blog.model.query.BlogFriendLinkPageQuery;
import com.klee.module.blog.model.vo.BlogFriendLinkVO;
import com.klee.module.blog.service.BlogFriendLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客友链")
@RequestMapping("/api/v1/blog/friend-links")
public class BlogFriendLinkController {

    private final BlogFriendLinkService friendLinkService;

    @Operation(summary = "友链分页列表")
    @PostMapping("/page")
    @SaCheckPermission("blog:friend-link:query")
    @Log(value = "友链分页列表", module = LogModuleEnum.BLOG)
    public PageResult<BlogFriendLinkVO> page(@RequestBody BlogFriendLinkPageQuery query) {
        return PageResult.success(friendLinkService.pageFriendLinks(query));
    }

    @Operation(summary = "新增友链")
    @PostMapping
    @SaCheckPermission("blog:friend-link:add")
    @Log(value = "新增友链", module = LogModuleEnum.BLOG)
    public Result<Boolean> save(@Valid @RequestBody BlogFriendLinkForm form) {
        return Result.judge(friendLinkService.saveFriendLink(form));
    }

    @Operation(summary = "修改友链")
    @PutMapping("/{id}")
    @SaCheckPermission("blog:friend-link:update")
    @Log(value = "修改友链", module = LogModuleEnum.BLOG)
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody BlogFriendLinkForm form) {
        return Result.judge(friendLinkService.updateFriendLink(id, form));
    }

    @Operation(summary = "删除友链")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:friend-link:delete")
    @Log(value = "删除友链", module = LogModuleEnum.BLOG)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.judge(friendLinkService.deleteFriendLink(id));
    }

    @Operation(summary = "前台: 获取友链列表")
    @GetMapping("/list")
    @SaIgnore
    @Log(value = "获取友链列表", module = LogModuleEnum.BLOG)
    public Result<List<BlogFriendLinkVO>> list() {
        return Result.success(friendLinkService.listEnabled());
    }
}