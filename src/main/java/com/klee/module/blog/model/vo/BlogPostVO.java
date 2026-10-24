package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "博客文章VO")
public class BlogPostVO {
    @Schema(description = "文章ID")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "摘要")
    private String summary;
    @Schema(description = "内容(可能为Markdown或HTML)")
    private String content;
    @Schema(description = "封面图URL")
    private String coverImage; // 前端保持不变: URL
    @Schema(description = "封面图文件ID")
    private Long coverImageFileId; // 新增: 方便后续操作
    @Schema(description = "作者用户ID")
    private Long authorId;
    @Schema(description = "访问量")
    private Long viewCount;
    @Schema(description = "置顶 0-否 1-是")
    private Integer pinned; // 新增字段
    @Schema(description = "推荐 0-否 1-是")
    private Integer isRecommend;
    @Schema(description = "状态 0-草稿 1-已发布")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "标签集合")
    private List<BlogTagVO> tags;
}
