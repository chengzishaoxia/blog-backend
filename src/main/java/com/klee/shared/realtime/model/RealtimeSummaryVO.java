package com.klee.shared.realtime.model;

import lombok.Data;

@Data
public class RealtimeSummaryVO {

    private int onlineUserCount;

    private int connectionCount;

    private int adminConnectionCount;

    private int chatConnectionCount;
}