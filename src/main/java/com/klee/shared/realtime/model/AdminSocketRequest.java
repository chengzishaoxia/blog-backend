package com.klee.shared.realtime.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class AdminSocketRequest {

    private String type;

    private String topic;

    private JsonNode payload;
}