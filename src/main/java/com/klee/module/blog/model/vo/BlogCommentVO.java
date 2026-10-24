package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "博客评论VO")
public class BlogCommentVO {
    @Schema(description = "评论ID")
    private Long id;
    @Schema(description = "文章ID")
    private Long postId;
    @Schema(description = "父评论ID(0为根)")
    private Long parentId;
    @Schema(description = "评论用户ID")
    private Long userId;
    // 新增：评论用户昵称
    @Schema(description = "评论用户昵称")
    private String nickname;
    // 新增：评论用户头像URL
    @Schema(description = "评论用户头像URL")
    private String avatar;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "点赞数")
    private Long likeCount;
    @Schema(description = "状态 1-显示 0-隐藏")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "子评论集合")
    private List<BlogCommentVO> children;
}
