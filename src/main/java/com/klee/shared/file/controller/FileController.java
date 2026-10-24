package com.klee.shared.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 用于返回分页VO
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.model.form.FileDownloadForm;
import com.klee.shared.file.model.form.FileInfoUpdateForm;
import com.klee.shared.file.model.query.FileInfoPageQuery;
import com.klee.shared.file.model.vo.FileInfoPageVO;
import com.klee.shared.file.service.FileInfoService;
import com.klee.shared.file.service.FileService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件控制层
 */
@Tag(name = "07.文件接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileInfoService fileInfoService; // 使用 service 封装逻辑

    @PostMapping
    @Operation(summary = "文件上传")
    @SaCheckPermission("file:upload")
    public Result<FileInfo> uploadFile(
            @Parameter(
                    name = "file",
                    description = "表单文件对象",
                    required = true,
                    in = ParameterIn.DEFAULT,
                    schema = @Schema(name = "file", format = "binary")
            )
            @RequestPart(value = "file") MultipartFile file,
            @Parameter(description = "文件用途类型，例如: AVATAR, EMOJI, BLOG_COVER, CAROUSEL")
            @RequestParam(value = "usageType", required = false) String usageType
    ) {
        FileInfo fileInfo = fileService.uploadFile(file, usageType);
        return Result.success(fileInfo);
    }

    @PostMapping("/page")
    @Operation(summary = "文件分页查询")
    @SaCheckPermission("file:query")
    public PageResult<FileInfoPageVO> page(@RequestBody FileInfoPageQuery query) {
        Page<FileInfoPageVO> voPage = fileInfoService.pageVo(query);
        return PageResult.success(voPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "文件详情")
    @SaCheckPermission("file:query")
    public Result<FileInfoPageVO> get(@PathVariable Long id) {
        FileInfoPageVO vo = fileInfoService.getVo(id);
        return vo == null ? Result.failed("文件不存在") : Result.success(vo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文件信息")
    @SaCheckPermission("file:edit")
    public Result<?> update(@PathVariable Long id, @Validated @RequestBody FileInfoUpdateForm form) {
        boolean ok = fileInfoService.update(id, form);
        return Result.judge(ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件记录")
    @SaCheckPermission("file:delete")
    public Result<?> deleteById(@PathVariable Long id) {
        boolean ok = fileInfoService.delete(id);
        return Result.judge(ok);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件记录")
    @SaCheckPermission("file:delete")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        boolean ok = fileInfoService.batchDelete(ids);
        return Result.judge(ok);
    }


    @PostMapping("/download")
    @Operation(summary = "文件下载记录")
    @Log( value = "文件下载",module = LogModuleEnum.OTHER)
    public Result<String> downloadFile(@RequestBody FileDownloadForm form) {
        return Result.success(form.getFileName());
    }
}
