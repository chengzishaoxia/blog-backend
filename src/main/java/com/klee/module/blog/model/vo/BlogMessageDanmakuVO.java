package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "留言弹幕VO")
public class BlogMessageDanmakuVO {

    @Schema(description = "留言ID")
    private Long id;

    @Schema(description = "留言用户昵称")
    private String nickname;

    @Schema(description = "留言用户头像")
    private String avatar;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
