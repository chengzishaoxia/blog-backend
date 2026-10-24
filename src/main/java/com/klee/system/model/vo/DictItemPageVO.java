package com.klee.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 字典项分页视图对象
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Data
@Schema(description = "字典项分页")
public class DictItemPageVO {

    @Schema(description = "字典项ID")
    private Long id;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典标签")
    private String label;

    @Schema(description = "字典值")
    private String value;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态（1:启用，0:禁用）")
    private Integer status;

}
