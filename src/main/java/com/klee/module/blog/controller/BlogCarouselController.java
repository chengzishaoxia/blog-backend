package com.klee.module.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.module.blog.model.form.BlogCarouselForm;
import com.klee.module.blog.model.query.BlogCarouselPageQuery;
import com.klee.module.blog.model.vo.BlogCarouselVO;
import com.klee.module.blog.service.BlogCarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客轮播图")
@RequestMapping("/api/v1/blog/carousels")
public class BlogCarouselController {

    private final BlogCarouselService carouselService;

    @Operation(summary = "轮播图分页列表")
    @PostMapping("/page")
    @SaCheckPermission("blog:carousel:query")
    @Log(value = "轮播图分页列表", module = LogModuleEnum.BLOG)
    public PageResult<BlogCarouselVO> page(@RequestBody BlogCarouselPageQuery query) {
        IPage<BlogCarouselVO> page = carouselService.pageCarousels(query);
        return PageResult.success(page);
    }

    @Operation(summary = "新增轮播图")
    @PostMapping
    @SaCheckPermission("blog:carousel:add")
    @Log(value = "新增轮播图", module = LogModuleEnum.BLOG)
    public Result<Boolean> save(@Valid @RequestBody BlogCarouselForm form) {
        return Result.judge(carouselService.saveCarousel(form));
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/{id}")
    @SaCheckPermission("blog:carousel:update")
    @Log(value = "更新轮播图", module = LogModuleEnum.BLOG)
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody BlogCarouselForm form) {
        return Result.judge(carouselService.updateCarousel(id, form));
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:carousel:delete")
    @Log(value = "删除轮播图", module = LogModuleEnum.BLOG)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.judge(carouselService.deleteCarousel(id));
    }

    @Operation(summary = "前台: 获取轮播图列表")
    @GetMapping("/list")
    @Log(value = "前台获取轮播图列表", module = LogModuleEnum.BLOG)
    @SaIgnore
    public Result<List<BlogCarouselVO>> list() {
        return Result.success(carouselService.listEnabled());
    }
}


