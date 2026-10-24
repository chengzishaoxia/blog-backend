package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 站点轮播图实体
 * 表: blog_carousel
 * 仅保存文件记录ID，创建人/更新人，逻辑删除标识。
 */
@Getter
@Setter
@TableName("blog_carousel")
public class BlogCarousel extends BaseEntity {

    /** 轮播图文件记录ID (关联 file_info.id) */
    private Long fileId;

    /** 创建人ID */
    private Long createBy;

    /** 更新人ID */
    private Long updateBy;

    /** 是否已删除(0-未删除 1-已删除) */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}

