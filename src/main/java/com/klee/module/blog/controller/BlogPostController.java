package com.klee.module.blog.controller;

import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.module.blog.enums.BlogPostStatusEnum;
import com.klee.module.blog.model.form.BlogPostForm;
import com.klee.module.blog.model.query.BlogPostPageQuery;
import com.klee.module.blog.model.vo.BlogPostVO;
import com.klee.module.blog.model.vo.BlogArchiveGroupVO;
import com.klee.module.blog.service.BlogPostService;
import com.klee.system.model.dto.UserExportDTO;
import com.klee.system.model.query.UserPageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "博客文章")
@RequestMapping("/api/v1/blog/posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Operation(summary = "博客文章分页")
    @PostMapping("/page")
    @SaIgnore // 不需要登录与权限校验
    @Log(value = "博客文章分页", module = LogModuleEnum.BLOG)
    public PageResult<BlogPostVO> page(@RequestBody BlogPostPageQuery query) {
        query.setStatus(BlogPostStatusEnum.PUBLISHED.getValue());
        IPage<BlogPostVO> page = blogPostService.page(query);
        return PageResult.success(page);
    }

    @Operation(summary = "管理端: 博客文章分页")
    @PostMapping("/manage/page")
    @SaCheckPermission("blog:write:query")
    @Log(value = "管理端博客文章分页", module = LogModuleEnum.BLOG)
    public PageResult<BlogPostVO> managePage(@RequestBody BlogPostPageQuery query) {
        IPage<BlogPostVO> page = blogPostService.page(query);
        return PageResult.success(page);
    }

    @Operation(summary = "推荐文章列表")
    @GetMapping("/recommend")
    @SaIgnore
    @Log(value = "推荐文章列表", module = LogModuleEnum.BLOG)
    public Result<List<BlogPostVO>> recommend() {
        return Result.success(blogPostService.recommendList());
    }

    @Operation(summary = "获取文章详情并+1访问量")
    @GetMapping("/{id}")
    @SaIgnore
    @Log(value = "获取文章详情", module = LogModuleEnum.BLOG)
    public Result<BlogPostVO> get(@PathVariable Long id) {
        return Result.success(blogPostService.getPost(id, true));
    }

    @Operation(summary = "获取文章详情(不增加访问量)")
    @GetMapping("/{id}/detail")
    @SaIgnore
    @Log(value = "获取文章详情(不增加访问量)", module = LogModuleEnum.BLOG)
    public Result<BlogPostVO> getDetail(@PathVariable Long id) {
        return Result.success(blogPostService.getPost(id, false));
    }

    @Operation(summary = "新增或修改文章(存在ID则为修改,否则新增)")
    @PostMapping("/upsert")
    @SaCheckPermission("blog:write:upsert")
    @Log(value = "新增或修改文章", module = LogModuleEnum.BLOG)
    public Result<?> upsert(@Valid @RequestBody BlogPostForm form) {
        return Result.judge(blogPostService.upsertPost(form));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    @SaCheckPermission("blog:write:delete")
    @Log(value = "删除文章", module = LogModuleEnum.BLOG)
    public Result<?> delete(@PathVariable Long id) {
        return Result.judge(blogPostService.deletePost(id));
    }

    @Operation(summary = "文章归档(按年月分组)")
    @GetMapping("/archives")
    @SaIgnore
    @Log(value = "文章归档", module = LogModuleEnum.BLOG)
    public Result<List<BlogArchiveGroupVO>> archives() {
        return Result.success(blogPostService.listArchives());
    }

    //导出 管理端博客文章列表   暂时不需要文章导出
    @Operation(summary = "导出博客文章")
    @GetMapping("/export")
    @SaCheckPermission("blog:write:export")
    @Log(value = "导出博客文章", module = LogModuleEnum.BLOG)
    public void exportBlog(UserPageQuery queryParams, HttpServletResponse response) throws IOException {
//        String fileName = "文章列表.xlsx";
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
////
//        List<UserExportDTO> exportUserList = userService.listExportUsers(queryParams);
//        EasyExcel.write(response.getOutputStream(), UserExportDTO.class).sheet("用户列表")
//                .doWrite(exportUserList);
    }

}
