package com.klee.module.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.Result;
import com.klee.module.blog.model.form.BlogSiteConfigForm;
import com.klee.module.blog.model.vo.BlogSiteConfigVO;
import com.klee.module.blog.service.BlogSiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客站点配置")
@RequestMapping("/api/v1/blog/site-config")
public class BlogSiteConfigController {

    private final BlogSiteConfigService blogSiteConfigService;

    @Operation(summary = "前台: 获取站点配置")
    @GetMapping
    @SaIgnore
    public Result<BlogSiteConfigVO> getSiteConfig() {
        return Result.success(blogSiteConfigService.getSiteConfig());
    }

    @Operation(summary = "后台: 获取站点配置")
    @GetMapping("/detail")
    @SaCheckPermission("blog:site-config:query")
    public Result<BlogSiteConfigVO> getSiteConfigDetail() {
        return Result.success(blogSiteConfigService.getSiteConfig());
    }

    @Operation(summary = "后台: 更新站点配置")
    @PutMapping
    @SaCheckPermission("blog:site-config:update")
    @Log(value = "更新站点配置", module = LogModuleEnum.BLOG)
    public Result<Boolean> updateSiteConfig(@RequestBody BlogSiteConfigForm form) {
        return Result.judge(blogSiteConfigService.updateSiteConfig(form));
    }
}