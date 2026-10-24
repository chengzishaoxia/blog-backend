package com.klee.module.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.klee.common.annotation.Log;
import com.klee.common.enums.StatusEnum;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.module.blog.model.form.BlogTagForm;
import com.klee.module.blog.model.query.BlogTagPageQuery;
import com.klee.module.blog.model.vo.BlogTagVO;
import com.klee.module.blog.service.BlogTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客标签")
@RequestMapping("/api/v1/blog/tags")
public class BlogTagController {

    private final BlogTagService tagService;

    @Operation(summary = "标签分页列表")
    @PostMapping("/page")
    @SaIgnore
    @Log(value = "标签分页列表", module = LogModuleEnum.BLOG)
    public PageResult<BlogTagVO> page(@RequestBody BlogTagPageQuery queryParams) {
        queryParams.setStatus(StatusEnum.ENABLE.getValue());
        return PageResult.success(tagService.pageTags(queryParams));
    }

    @Operation(summary = "管理端: 标签分页列表")
    @PostMapping("/manage/page")
    @SaCheckPermission("blog:tag:query")
    @Log(value = "管理端标签分页列表", module = LogModuleEnum.BLOG)
    public PageResult<BlogTagVO> managePage(@RequestBody BlogTagPageQuery queryParams) {
        return PageResult.success(tagService.pageTags(queryParams));
    }

    @Operation(summary = "新增标签")
    @PostMapping
    @SaCheckPermission("blog:tag:add")
    @Log(value = "新增标签", module = LogModuleEnum.BLOG)
    public Result<?> save(@Valid @RequestBody BlogTagForm form) {
        return Result.judge(tagService.saveTag(form));
    }

    @Operation(summary = "修改标签")
    @PutMapping("/{id}")
    @SaCheckPermission("blog:tag:update")
    @Log(value = "修改标签", module = LogModuleEnum.BLOG)
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody BlogTagForm form) {
        return Result.judge(tagService.updateTag(id, form));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:tag:delete")
    @Log(value = "删除标签", module = LogModuleEnum.BLOG)
    public Result<?> delete(@PathVariable Long id) {
        return Result.judge(tagService.deleteTag(id));
    }
}
