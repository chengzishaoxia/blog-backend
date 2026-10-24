package com.klee.shared.realtime.store;

import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;

import java.util.List;

public interface RealtimeConnectionSnapshotStore {

    void save(RealtimeConnectionSnapshot snapshot, long ttlMillis);

    void remove(String connectionId);

    List<RealtimeConnectionSnapshot> listActive();
}