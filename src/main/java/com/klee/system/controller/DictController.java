package com.klee.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.common.model.Option;
import com.klee.common.result.PageResult;
import com.klee.common.result.Result;
import com.klee.common.enums.LogModuleEnum;
import com.klee.system.model.form.DictItemForm;
import com.klee.system.model.query.DictItemPageQuery;
import com.klee.system.model.query.DictPageQuery;
import com.klee.system.model.vo.DictItemOptionVO;
import com.klee.system.model.vo.DictItemPageVO;
import com.klee.system.model.vo.DictPageVO;
import com.klee.common.annotation.RepeatSubmit;
import com.klee.system.model.form.DictForm;
import com.klee.common.annotation.Log;
import com.klee.system.service.DictItemService;
import com.klee.system.service.DictService;
import com.klee.system.service.WebSocketService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 字典控制层
 *
 * @author Ray.Hao
 * @since 2.9.0
 */
@Tag(name = "06.字典接口")
@RestController
@SuppressWarnings("SpellCheckingInspection")
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;
    private final DictItemService dictItemService;
    private final WebSocketService webSocketService;

    //---------------------------------------------------
    // 字典相关接口
    //---------------------------------------------------
    @Operation(summary = "字典分页列表")
    @GetMapping("/page")
    @Log( value = "字典分页列表",module = LogModuleEnum.DICT)
    public PageResult<DictPageVO> getDictPage(
            DictPageQuery queryParams
    ) {
        Page<DictPageVO> result = dictService.getDictPage(queryParams);
        return PageResult.success(result);
    }


    @Operation(summary = "字典列表")
    @GetMapping
    public Result<List<Option<String>>> getDictList() {
        List<Option<String>> list = dictService.getDictList();
        return Result.success(list);
    }

    @Operation(summary = "获取字典表单数据")
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description = "字典ID") @PathVariable Long id
    ) {
        DictForm formData = dictService.getDictForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增字典")
    @PostMapping
    @SaCheckPermission("sys:dict:add")
    @RepeatSubmit
    public Result<?> saveDict(@Valid @RequestBody DictForm formData) {
        boolean result = dictService.saveDict(formData);
        // 发送字典更新通知
        if (result) {
            webSocketService.broadcastDictChange(formData.getDictCode());
        }
        return Result.judge(result);
    }

    @Operation(summary = "修改字典")
    @PutMapping("/{id}")
    @SaCheckPermission("sys:dict:edit")
    public Result<?> updateDict(
            @PathVariable Long id,
            @RequestBody DictForm dictForm
    ) {
        boolean status = dictService.updateDict(id, dictForm);
        // 发送字典更新通知
        if (status && dictForm.getDictCode() != null) {
          webSocketService.broadcastDictChange(dictForm.getDictCode());
        }
        return Result.judge(status);
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("sys:dict:delete")
    public Result<?> deleteDictionaries(
            @Parameter(description = "字典ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        // 获取字典编码列表，用于发送删除通知
        List<String> dictCodes = dictService.getDictCodesByIds(Arrays.stream(ids.split(",")).toList());

        dictService.deleteDictByIds(Arrays.stream(ids.split(",")).toList());

        // 发送字典删除通知
        for (String dictCode : dictCodes) {
          webSocketService.broadcastDictChange(dictCode);
        }

        return Result.success();
    }


    //---------------------------------------------------
    // 字典项相关接口
    //---------------------------------------------------
    @Operation(summary = "字典项分页列表")
    @GetMapping("/{dictCode}/items/page")
    public PageResult<DictItemPageVO> getDictItemPage(
            @PathVariable String dictCode,
            DictItemPageQuery queryParams
    ) {
        queryParams.setDictCode(dictCode);
        Page<DictItemPageVO> result = dictItemService.getDictItemPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典项列表")
    @GetMapping("/{dictCode}/items")
    public Result<List<DictItemOptionVO>> getDictItems(
            @Parameter(description = "字典编码") @PathVariable String dictCode
    ) {
        List<DictItemOptionVO> list = dictItemService.getDictItems(dictCode);
        return Result.success(list);
    }

    @Operation(summary = "新增字典项")
    @PostMapping("/{dictCode}/items")
    @SaCheckPermission("sys:dict-item:add")
    @RepeatSubmit
    public Result<Void> saveDictItem(
            @PathVariable String dictCode,
            @Valid @RequestBody DictItemForm formData
    ) {
        formData.setDictCode(dictCode);
        boolean result = dictItemService.saveDictItem(formData);

        // 发送字典更新通知
        if (result) {
          webSocketService.broadcastDictChange(dictCode);
        }

        return Result.judge(result);
    }

    @Operation(summary = "字典项表单数据")
    @GetMapping("/{dictCode}/items/{itemId}/form")
    public Result<DictItemForm> getDictItemForm(
            @PathVariable String dictCode,
            @Parameter(description = "字典项ID") @PathVariable Long itemId
    ) {
        DictItemForm formData = dictItemService.getDictItemForm(itemId);
        return Result.success(formData);
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/{dictCode}/items/{itemId}")
    @SaCheckPermission("sys:dict-item:edit")
    @RepeatSubmit
    public Result<?> updateDictItem(
            @PathVariable String dictCode,
            @PathVariable Long itemId,
            @RequestBody DictItemForm formData
    ) {
        formData.setId(itemId);
        formData.setDictCode(dictCode);
        boolean status = dictItemService.updateDictItem(formData);

        // 发送字典更新通知
        if (status) {
            webSocketService.broadcastDictChange(dictCode);
        }

        return Result.judge(status);
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/{dictCode}/items/{itemIds}")
    @SaCheckPermission("sys:dict-item:delete")
    public Result<Void> deleteDictItems(
            @PathVariable String dictCode,
            @Parameter(description = "字典ID，多个以英文逗号(,)拼接") @PathVariable String itemIds
    ) {
        dictItemService.deleteDictItemByIds(itemIds);

        // 发送字典更新通知
        webSocketService.broadcastDictChange(dictCode);

        return Result.success();
    }

}
