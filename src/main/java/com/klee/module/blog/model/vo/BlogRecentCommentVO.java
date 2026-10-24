package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "博客最新评论")
public class BlogRecentCommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long postId;

    @Schema(description = "文章标题")
    private String postTitle;

    @Schema(description = "评论昵称")
    private String nickname;

    @Schema(description = "评论头像")
    private String avatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论时间")
    private LocalDateTime createTime;
}