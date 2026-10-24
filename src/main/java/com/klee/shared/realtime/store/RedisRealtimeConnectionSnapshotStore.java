package com.klee.shared.realtime.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisRealtimeConnectionSnapshotStore implements RealtimeConnectionSnapshotStore {

    private static final String ACTIVE_SET_KEY = "realtime:connections:active";
    private static final String CONNECTION_KEY_PREFIX = "realtime:connections:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(RealtimeConnectionSnapshot snapshot, long ttlMillis) {
        if (snapshot == null || snapshot.getConnectionId() == null) {
            return;
        }
        String key = CONNECTION_KEY_PREFIX + snapshot.getConnectionId();
        try {
            String json = objectMapper.writeValueAsString(snapshot);
            redisTemplate.opsForValue().set(key, json, ttlMillis, TimeUnit.MILLISECONDS);
            redisTemplate.opsForSet().add(ACTIVE_SET_KEY, snapshot.getConnectionId());
            redisTemplate.expire(ACTIVE_SET_KEY, Math.max(ttlMillis * 2, TimeUnit.MINUTES.toMillis(10)), TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.warn("保存 WebSocket 连接快照失败 connectionId={} ex={}", snapshot.getConnectionId(), ex.getMessage());
        }
    }

    @Override
    public void remove(String connectionId) {
        if (connectionId == null) {
            return;
        }
        redisTemplate.delete(CONNECTION_KEY_PREFIX + connectionId);
        redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, connectionId);
    }

    @Override
    public List<RealtimeConnectionSnapshot> listActive() {
        Set<Object> ids = redisTemplate.opsForSet().members(ACTIVE_SET_KEY);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<RealtimeConnectionSnapshot> snapshots = new ArrayList<>();
        for (Object rawId : ids) {
            if (rawId == null) {
                continue;
            }
            String connectionId = String.valueOf(rawId);
            Object rawValue = redisTemplate.opsForValue().get(CONNECTION_KEY_PREFIX + connectionId);
            if (rawValue == null) {
                redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, connectionId);
                continue;
            }
            try {
                RealtimeConnectionSnapshot snapshot = objectMapper.readValue(String.valueOf(rawValue), new TypeReference<>() {});
                snapshots.add(snapshot);
            } catch (Exception ex) {
                log.warn("解析 WebSocket 连接快照失败 connectionId={} ex={}", connectionId, ex.getMessage());
                redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, connectionId);
            }
        }
        return snapshots;
    }
}