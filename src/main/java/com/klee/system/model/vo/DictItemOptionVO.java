package com.klee.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典项下拉视图对象
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Data
@Schema(description = "字典项下拉")
public class DictItemOptionVO {

    @Schema(description = "字典项值")
    private String value;

    @Schema(description = "字典项标签")
    private String label;

    @Schema(description = "标签类型")
    private String tagType;

}
