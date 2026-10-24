package com.klee.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName blog_visit_log
 */
@Data
@TableName(value ="blog_visit_log")
public class BlogVisitLog implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String ipAddress;

    /**
     *
     */
    private String visitInfo;

    /**
     * 
     */
    private Date visitDate;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}