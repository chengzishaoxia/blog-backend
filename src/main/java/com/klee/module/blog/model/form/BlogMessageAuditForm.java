package com.klee.module.blog.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "留言审核表单")
public class BlogMessageAuditForm implements Serializable {

    @NotNull
    @Schema(description = "审核状态 1-通过 2-驳回")
    private Integer auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;
}
