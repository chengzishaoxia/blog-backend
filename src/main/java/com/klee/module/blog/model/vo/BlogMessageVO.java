package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "留言管理VO")
public class BlogMessageVO {

    @Schema(description = "留言ID")
    private Long id;

    @Schema(description = "留言用户ID")
    private Long userId;

    @Schema(description = "留言头像文件ID")
    private Long avatarFileId;

    @Schema(description = "留言用户昵称")
    private String nickname;

    @Schema(description = "提交IP")
    private String submitIp;

    @Schema(description = "IP归属地")
    private String ipRegion;

    @Schema(description = "留言用户头像")
    private String avatar;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "审核状态 0-待审核 1-通过 2-驳回")
    private Integer auditStatus;

    @Schema(description = "审核人ID")
    private Long auditBy;

    @Schema(description = "审核人昵称")
    private String auditByName;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
