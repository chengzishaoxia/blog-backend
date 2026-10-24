package com.klee.shared.realtime.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminSocketEnvelope {

    private String type;

    private String topic;

    private Object payload;

    private String message;

    private long timestamp;
}