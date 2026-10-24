package com.klee.module.blog.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 按年份-月份分组的文章归档
 */
@Data
public class BlogArchiveGroupVO {
    private Integer year;
    private Integer month; // 1-12
    private Integer count; // 本月文章数量
    private List<BlogArchivePostVO> posts; // 该月份的文章列表（可按时间倒序）
}

