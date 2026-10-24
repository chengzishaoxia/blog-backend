package com.klee.shared.file.model.query;

import com.klee.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文件信息分页查询
 */
@Getter
@Setter
@Schema(description = "文件信息分页查询参数")
public class FileInfoPageQuery extends BasePageQuery {

    @Schema(description = "关键字(名称模糊匹配)")
    private String keywords;

    @Schema(description = "用途类型，(如: AVATAR, EMOJI, BLOG_COVER, CAROUSEL, OTHER)")
    private String usageType;

    @Schema(description = "上传者用户ID")
    private Long uploaderUserId;

    @Schema(description = "创建时间范围: 开始(yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime startTime;

    @Schema(description = "创建时间范围: 结束(yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime endTime;

    @Schema(description = "是否仅返回图片文件 (contentType 以 image/ 开头)")
    private Boolean onlyImages;
}
