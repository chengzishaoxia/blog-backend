package com.klee.shared.file.enums;

/**
 * 文件用途类型枚举
 */
public enum FileUsageTypeEnum {
    AVATAR,
    EMOJI,
    BLOG_COVER,
    CAROUSEL,
    SITE_CONFIG,
    OTHER;

    public static String normalize(String usageType) {
        if (usageType == null || usageType.isBlank()) {
            return OTHER.name();
        }
        try {
            return FileUsageTypeEnum.valueOf(usageType.toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            return OTHER.name();
        }
    }
}


