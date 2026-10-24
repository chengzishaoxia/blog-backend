package com.klee.shared.realtime.model;

import lombok.Data;

import java.util.Set;

@Data
public class RealtimeConnectionDetailVO {

    private String connectionId;

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private String channelType;

    private String path;

    private String remoteAddress;

    private long connectedAt;

    private long lastSeenAt;

    private Set<String> subscriptions;

    private boolean chatBanned;

    private String chatBanReason;
}