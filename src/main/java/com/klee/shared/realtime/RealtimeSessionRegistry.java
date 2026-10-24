package com.klee.shared.realtime;

import com.klee.shared.realtime.model.RealtimeChannelType;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import com.klee.shared.realtime.store.RealtimeConnectionSnapshotStore;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RealtimeSessionRegistry {

    private final RealtimeConnectionSnapshotStore snapshotStore;

    private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Map<Channel, RealtimeConnectionSnapshot> channelSnapshots = new ConcurrentHashMap<>();
    private final Map<String, Channel> channelsByConnectionId = new ConcurrentHashMap<>();
    private final Map<Long, Set<Channel>> channelsByUser = new ConcurrentHashMap<>();
    private final Map<String, Set<Channel>> channelsByTopic = new ConcurrentHashMap<>();

    public void register(Channel channel, RealtimeConnectionSnapshot snapshot, long ttlMillis) {
        allChannels.add(channel);
        channelSnapshots.put(channel, snapshot);
        channelsByConnectionId.put(snapshot.getConnectionId(), channel);
        channelsByUser.computeIfAbsent(snapshot.getUserId(), key -> ConcurrentHashMap.newKeySet()).add(channel);
        snapshotStore.save(snapshot, ttlMillis);
    }

    public RealtimeConnectionSnapshot unregister(Channel channel) {
        if (channel == null) {
            return null;
        }
        allChannels.remove(channel);
        RealtimeConnectionSnapshot snapshot = channelSnapshots.remove(channel);
        if (snapshot == null) {
            return null;
        }
        channelsByConnectionId.remove(snapshot.getConnectionId());
        Set<Channel> userChannels = channelsByUser.get(snapshot.getUserId());
        if (userChannels != null) {
            userChannels.remove(channel);
            if (userChannels.isEmpty()) {
                channelsByUser.remove(snapshot.getUserId());
            }
        }
        for (String topic : new LinkedHashSet<>(snapshot.getSubscriptions())) {
            unsubscribe(channel, topic);
        }
        snapshotStore.remove(snapshot.getConnectionId());
        return snapshot;
    }

    public void touch(Channel channel, long ttlMillis) {
        RealtimeConnectionSnapshot snapshot = channelSnapshots.get(channel);
        if (snapshot == null) {
            return;
        }
        snapshot.setLastSeenAt(System.currentTimeMillis());
        snapshotStore.save(snapshot, ttlMillis);
    }

    public void subscribe(Channel channel, String topic, long ttlMillis) {
        RealtimeConnectionSnapshot snapshot = channelSnapshots.get(channel);
        if (snapshot == null || topic == null || topic.isBlank()) {
            return;
        }
        snapshot.getSubscriptions().add(topic);
        channelsByTopic.computeIfAbsent(topic, key -> ConcurrentHashMap.newKeySet()).add(channel);
        snapshot.setLastSeenAt(System.currentTimeMillis());
        snapshotStore.save(snapshot, ttlMillis);
    }

    public void unsubscribe(Channel channel, String topic) {
        RealtimeConnectionSnapshot snapshot = channelSnapshots.get(channel);
        if (snapshot == null || topic == null || topic.isBlank()) {
            return;
        }
        snapshot.getSubscriptions().remove(topic);
        Set<Channel> topicChannels = channelsByTopic.get(topic);
        if (topicChannels != null) {
            topicChannels.remove(channel);
            if (topicChannels.isEmpty()) {
                channelsByTopic.remove(topic);
            }
        }
    }

    public RealtimeConnectionSnapshot getSnapshot(Channel channel) {
        return channelSnapshots.get(channel);
    }

    public List<RealtimeConnectionSnapshot> getActiveSnapshots() {
        return new ArrayList<>(channelSnapshots.values());
    }

    public List<RealtimeConnectionSnapshot> getSnapshotsByType(RealtimeChannelType channelType) {
        return channelSnapshots.values().stream()
                .filter(snapshot -> snapshot.getChannelType() == channelType)
                .toList();
    }

    public Set<Channel> getChannelsByUser(Long userId, RealtimeChannelType channelType) {
        Set<Channel> channels = channelsByUser.get(userId);
        if (channels == null || channels.isEmpty()) {
            return Collections.emptySet();
        }
        if (channelType == null) {
            return Collections.unmodifiableSet(channels);
        }
        return channels.stream()
                .filter(channel -> {
                    RealtimeConnectionSnapshot snapshot = channelSnapshots.get(channel);
                    return snapshot != null && snapshot.getChannelType() == channelType;
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Channel> getChannelsByTopic(String topic) {
        Set<Channel> channels = channelsByTopic.get(topic);
        if (channels == null || channels.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(channels);
    }

    public Set<Channel> getChannelsByType(RealtimeChannelType channelType) {
        return channelSnapshots.entrySet().stream()
                .filter(entry -> entry.getValue().getChannelType() == channelType)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Channel> findChannels(Predicate<RealtimeConnectionSnapshot> predicate) {
        return channelSnapshots.entrySet().stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isUserOnline(Long userId, RealtimeChannelType channelType) {
        return !getChannelsByUser(userId, channelType).isEmpty();
    }

    public int countConnections(RealtimeChannelType channelType) {
        if (channelType == null) {
            return channelSnapshots.size();
        }
        return (int) channelSnapshots.values().stream()
                .filter(snapshot -> snapshot.getChannelType() == channelType)
                .count();
    }

    public int countUniqueUsers(RealtimeChannelType channelType) {
        return (int) channelSnapshots.values().stream()
                .filter(snapshot -> channelType == null || snapshot.getChannelType() == channelType)
                .map(RealtimeConnectionSnapshot::getUserId)
                .distinct()
                .count();
    }

    public Set<String> getConnectedUsernames(RealtimeChannelType channelType) {
        return channelSnapshots.values().stream()
                .filter(snapshot -> channelType == null || snapshot.getChannelType() == channelType)
                .map(RealtimeConnectionSnapshot::getUsername)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public ChannelGroup allChannels() {
        return allChannels;
    }

    public List<RealtimeConnectionSnapshot> listPersistedSnapshots() {
        return snapshotStore.listActive();
    }

    public boolean disconnect(String connectionId) {
        Channel channel = channelsByConnectionId.get(connectionId);
        if (channel == null) {
            snapshotStore.remove(connectionId);
            return false;
        }
        channel.close();
        return true;
    }

    public Channel getChannel(String connectionId) {
        return channelsByConnectionId.get(connectionId);
    }

    public RealtimeConnectionSnapshot getSnapshot(String connectionId) {
        Channel channel = channelsByConnectionId.get(connectionId);
        if (channel == null) {
            return null;
        }
        return channelSnapshots.get(channel);
    }
}