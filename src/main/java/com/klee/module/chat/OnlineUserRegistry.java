package com.klee.module.chat;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户注册表（内存结构）。
 * 变更说明（多会话支持）：
 *   - 原先是 userId -> Channel 单映射（仅保留最后一个连接）
 *   - 现在改为 userId -> Set<Channel>，允许同一用户多个浏览器/设备同时在线
 *   - 移除 allowMultiSession 相关逻辑，默认允许多会话
 * 线程安全：
 *   - ChannelGroup 内部线程安全
 *   - userChannels、channelUserMap 使用 ConcurrentHashMap；每个用户下的 Set 使用并发 Map 包装
 */
@Component // 作为 Spring Bean 管理
public class OnlineUserRegistry {
    /** 所有活跃的 Channel（用于广播） */
    private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /** 用户 ID -> 该用户所有 Channel 集合 */
    private final Map<Long, Set<Channel>> userChannels = new ConcurrentHashMap<>();
    /** Channel -> 用户 ID 反向索引（加速注销操作） */
    private final Map<Channel, Long> channelUserMap = new ConcurrentHashMap<>();

    /**
     * 注册用户与 Channel 绑定（允许同一用户多连接）。
     * @param userId 用户ID
     * @param channel Channel
     */
    public void register(Long userId, Channel channel) {
        if (userId == null || channel == null) return;
        allChannels.add(channel);
        // 获取/创建该用户的 Channel Set
        userChannels.computeIfAbsent(userId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(channel);
        channelUserMap.put(channel, userId);
    }

    /** 注销单个 Channel。 */
    public void unregister(Channel channel) {
        if (channel == null) return;
        allChannels.remove(channel);
        Long userId = channelUserMap.remove(channel);
        if (userId != null) {
            Set<Channel> set = userChannels.get(userId);
            if (set != null) {
                set.remove(channel);
                if (set.isEmpty()) userChannels.remove(userId);
            }
        } else {
            userChannels.values().forEach(s -> s.remove(channel));
        }
    }

    /** 获取该用户任意一个可用 Channel（兼容旧逻辑）。 */
    public Channel getAnyChannel(Long userId) {
        Set<Channel> set = userChannels.get(userId);
        if (set == null) return null;
        for (Channel ch : set) {
            if (ch != null && ch.isActive()) return ch;
        }
        return null;
    }

    /** 兼容旧代码：getChannel -> getAnyChannel */
    public Channel getChannel(Long userId) { return getAnyChannel(userId); }

    /** 获取所有 Channel 不可修改视图。 */
    public Set<Channel> getChannels(Long userId) {
        Set<Channel> set = userChannels.get(userId);
        if (set == null || set.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }

    /** 用户是否仍在线（至少一个 Channel 活跃记录）。 */
    public boolean isUserOnline(Long userId) { return userChannels.containsKey(userId); }

    /** 所有在线用户。 */
    public Set<Long> onlineUserIds() { return Collections.unmodifiableSet(userChannels.keySet()); }

    /** 全部 ChannelGroup。 */
    public ChannelGroup allChannels() { return allChannels; }
}
