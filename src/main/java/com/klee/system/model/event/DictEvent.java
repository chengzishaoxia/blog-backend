package com.klee.system.model.event;

import lombok.Data;

/**
 * 字典更新事件
 *
 * @author 可变羽翼核心
 * @since 2025/10/19
 */
@Data
public class DictEvent {
    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 时间戳
     */
    private long timestamp;

    public DictEvent(String dictCode) {
        this.dictCode = dictCode;
        this.timestamp = System.currentTimeMillis();
    }
}
