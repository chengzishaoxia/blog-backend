package com.klee.shared.realtime.model;

import com.klee.common.base.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RealtimeConnectionPageQuery extends BasePageQuery {

    private String keyword;

    private String channelType;
}