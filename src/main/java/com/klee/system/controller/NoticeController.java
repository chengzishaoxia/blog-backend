package com.klee.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.common.annotation.Log;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.system.model.form.NoticeForm;
import com.klee.system.model.query.NoticePageQuery;
import com.klee.system.model.vo.NoticeDetailVO;
import com.klee.system.model.vo.NoticePageVO;
import com.klee.system.model.vo.UserNoticePageVO;
import com.klee.system.service.NoticeService;
import com.klee.system.service.UserNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告前端控制层
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Tag(name = "09.通知公告")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    private final UserNoticeService userNoticeService;

    @Operation(summary = "通知公告分页列表")
    @GetMapping("/page")
    @SaCheckPermission("sys:notice:query")
    @Log(value = "通知公告分页列表", module = LogModuleEnum.OTHER)
    public PageResult<NoticePageVO> getNoticePage(NoticePageQuery queryParams) {
        IPage<NoticePageVO> result = noticeService.getNoticePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增通知公告")
    @PostMapping
    @SaCheckPermission("sys:notice:add")
    public Result<?> saveNotice(@RequestBody @Valid NoticeForm formData) {
        boolean result = noticeService.saveNotice(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取通知公告表单数据")
    @GetMapping("/{id}/form")
    @SaCheckPermission("sys:notice:edit")
    public Result<NoticeForm> getNoticeForm(
            @Parameter(description = "通知公告ID") @PathVariable Long id
    ) {
        NoticeForm formData = noticeService.getNoticeFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "阅读获取通知公告详情")
    @GetMapping("/{id}/detail")
    public Result<NoticeDetailVO> getNoticeDetail(
            @Parameter(description = "通知公告ID") @PathVariable Long id
    ) {
        NoticeDetailVO detailVO = noticeService.getNoticeDetail(id);
        return Result.success(detailVO);
    }

    @Operation(summary = "修改通知公告")
    @PutMapping(value = "/{id}")
    @SaCheckPermission("sys:notice:edit")
    public Result<Void> updateNotice(
            @Parameter(description = "通知公告ID") @PathVariable Long id,
            @RequestBody @Validated NoticeForm formData
    ) {
        boolean result = noticeService.updateNotice(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "发布通知公告")
    @PutMapping("/{id}/publish")
    @SaCheckPermission("sys:notice:publish")
    public Result<Void> publishNotice(
            @Parameter(description = "通知公告ID") @PathVariable Long id
    ) {
        boolean result = noticeService.publishNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "撤回通知公告")
    @PutMapping("/{id}/revoke")
    @SaCheckPermission("sys:notice:revoke")
    public Result<Void> revokeNotice(
            @Parameter(description = "通知公告ID") @PathVariable Long id
    ) {
        boolean result = noticeService.revokeNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("sys:notice:delete")
    public Result<Void> deleteNotices(
            @Parameter(description = "通知公告ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = noticeService.deleteNotices(ids);
        return Result.judge(result);
    }

    @Operation(summary = "全部已读")
    @PutMapping("/read-all")
    public Result<Void> readAll() {
        userNoticeService.readAll();
        return Result.success();
    }

    @Operation(summary = "获取我的通知公告分页列表")
    @GetMapping("/my-page")
    public PageResult<UserNoticePageVO> getMyNoticePage(
            NoticePageQuery queryParams
    ) {
        IPage<UserNoticePageVO> result = noticeService.getMyNoticePage(queryParams);
        return PageResult.success(result);
    }
}
