package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 留言板留言
 */
@Getter
@Setter
@TableName("blog_message")
public class BlogMessage extends BaseEntity {

    /** 留言用户ID */
    private Long userId;

    /** 留言头像文件ID */
    private Long avatarFileId;

    /** 留言展示昵称 */
    private String nickname;

    /** 提交IP */
    private String submitIp;

    /** IP归属地 */
    private String ipRegion;

    /** 留言内容 */
    private String content;

    /** 审核状态 0-待审核 1-通过 2-驳回 */
    private Integer auditStatus;

    /** 审核人ID */
    private Long auditBy;

    /** 审核备注 */
    private String auditRemark;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 逻辑删除 0-否 1-是 */
    private Integer isDeleted;
}
