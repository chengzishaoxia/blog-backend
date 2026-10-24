package com.klee.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单查询对象
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Schema(description ="菜单查询对象")
@Data
public class MenuQuery {

    @Schema(description="关键字(菜单名称)")
    private String keywords;

    @Schema(description="状态(1->显示；0->隐藏)")
    private Integer status;

}
