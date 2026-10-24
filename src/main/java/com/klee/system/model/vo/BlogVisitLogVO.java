package com.klee.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.klee.common.enums.LogModuleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * ip访问系统日志分页VO
 *
 * @author Ray
 * @since 2.10.0
 */
@Data
@Schema(description = "ip访问系统日志分页VO")
public class BlogVisitLogVO implements Serializable {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "IP 地址")
    private String ipAddress;

    @Schema(description = "访问信息")
    private String visitInfo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "访问日期")
    private Date visitDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
