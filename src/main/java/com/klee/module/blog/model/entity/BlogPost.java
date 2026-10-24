package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 博客文章实体
 */
@Getter
@Setter
@TableName("blog_post")
public class BlogPost extends BaseEntity {

    /** 标题 */
    private String title;
    /** 摘要 */
    private String summary;
    /** 内容 (Markdown/HTML) */
    private String content;
    /** 封面图文件ID (关联 file_info.id) */
    private Long coverImageFileId; // DB列名: cover_image_file_id (驼峰自动映射)
    /** 作者ID */
    private Long authorId;
    /** 访问量 */
    private Long viewCount;
    /** 置顶：0-否 1-是 */
    private Integer pinned; // 新增字段
    /** 推荐：0-否 1-是 */
    private Integer isRecommend;
    /** 状态：0-草稿 1-已发布 */
    private Integer status;
    /** 逻辑删除标志(0-未删除 1-已删除) */
    private Integer isDeleted;
}
