package com.klee.module.chat.server.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klee.module.chat.OnlineUserRegistry;
import com.klee.module.chat.config.ChatServerProperties;
import com.klee.module.chat.model.ChatMessage;
import com.klee.module.chat.model.MessageType;
import com.klee.module.chat.service.ChatHistoryService;
import com.klee.system.enums.RoleCodeEnum; // 导入角色枚举
import com.klee.system.model.entity.User;
import com.klee.system.service.UserService;
import com.klee.shared.file.mapper.FileInfoMapper; // 新增
import com.klee.shared.file.model.FileInfo; // 新增
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.cookie.Cookie; // 新增：Cookie 解析
import io.netty.handler.codec.http.cookie.ServerCookieDecoder; // 新增：Cookie 解析
import io.netty.util.AttributeKey;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component; // 新增

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Handle initial HTTP handshake, validate token then upgrade to WebSocket.
 * 多会话支持：
 * - 仅当用户第一次建立连接时广播 JOIN
 * - 仅当用户最后一个连接断开时广播 LEAVE
 * - 私聊消息回显/发送给双方所有会话
 * - 新增 IMAGE 类型：content 存放图片 URL（或其它媒资标识），与 CHAT 同步逻辑与权限校验一致
 */
@Slf4j
@RequiredArgsConstructor
@Component // 新增：Spring 注入
@ChannelHandler.Sharable // 新增：标记为可复用
public class AuthHandler extends SimpleChannelInboundHandler<Object> {

    public static final AttributeKey<Long> ATTR_USER_ID = AttributeKey.valueOf("USER_ID");
    public static final AttributeKey<String> ATTR_USERNAME = AttributeKey.valueOf("USERNAME");
    public static final AttributeKey<String> ATTR_NICKNAME = AttributeKey.valueOf("NICKNAME");
    public static final AttributeKey<String> ATTR_AVATAR = AttributeKey.valueOf("AVATAR");

    private final ChatServerProperties properties;
    private final OnlineUserRegistry registry;
    private final ObjectMapper objectMapper;
    private final ChatHistoryService historyService;
    private final UserService userService;
    private final StpInterface stpInterface; // 新增：直接查询权限与角色
    private final FileInfoMapper fileInfoMapper; // 新增：用于头像URL解析

    // 移除实例字段 handshaker，改用 Channel Attribute 保存
    private static final AttributeKey<WebSocketServerHandshaker> ATTR_HANDSHAKER = AttributeKey.valueOf("HANDSHAKER");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            handleHttpRequest(ctx, request);
            return;
        }
        if (msg instanceof WebSocketFrame frame) {
            handleWebSocketFrame(ctx, frame);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 1. 基础合法性校验：是否是 websocket 升级请求
        if (!req.decoderResult().isSuccess() || (!"websocket".equalsIgnoreCase(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 2. 校验路径是否匹配配置的 WebSocket 路径
        URI uri = URI.create(req.uri());
        if (!uri.getPath().equals(properties.getPath())) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            return;
        }
        // 3. 获取 token：仅从 Cookie 读取(Sa-Token tokenName)
        String token = null;
        String tokenName = SaManager.getConfig().getTokenName();
        String cookieHeader = req.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null) {
            try {
                Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
                // 读取配置文件的tokenName
                for (Cookie c : cookies) {
                    if (StringUtil.equals(tokenName, c.name())) {
                        token = c.value();
                        break;
                    }
                }
            } catch (Exception e) {
                log.debug("[CHAT][COOKIE-DECODE-FAIL] raw={} error={}", cookieHeader, e.getMessage());
            }
        }
        Long userId;
        try {
            Object loginId = (token != null) ? StpUtil.getLoginIdByToken(token) : StpUtil.getLoginIdDefaultNull();
            if (loginId == null) {
                sendHttpResponse(ctx, req, json(HttpResponseStatus.UNAUTHORIZED, "NOT_LOGIN"));
                return;
            }
            if (loginId instanceof Long l) userId = l;
            else userId = Long.parseLong(loginId.toString());
        } catch (NotLoginException e) {
            log.debug("Chat auth failed: {}", e.getMessage());
            sendHttpResponse(ctx, req, json(HttpResponseStatus.UNAUTHORIZED, "NOT_LOGIN"));
            return;
        } catch (Exception e) {
            log.error("Chat auth error", e);
            sendHttpResponse(ctx, req, json(HttpResponseStatus.INTERNAL_SERVER_ERROR, "AUTH_ERROR"));
            return;
        }
        // 缓存用户元信息
        try {
            User u = userService.getById(userId);
            if (u != null) {
                ctx.channel().attr(ATTR_USERNAME).set(u.getUsername());
                ctx.channel().attr(ATTR_NICKNAME).set(u.getNickname());
                String avatarUrl = null;
                if (u.getAvatarFileId() != null) {
                    FileInfo fi = fileInfoMapper.selectById(u.getAvatarFileId());
                    if (fi != null) avatarUrl = fi.getUrl();
                }
                ctx.channel().attr(ATTR_AVATAR).set(avatarUrl);
            }
        } catch (Exception e) {
            log.warn("Load user info failed userId={}", userId, e);
        }
        boolean firstConnection = !registry.isUserOnline(userId); // 记录是否首个会话
        ctx.channel().attr(ATTR_USER_ID).set(userId);
        registry.register(userId, ctx.channel());
        // 6. 手动握手（不使用 WebSocketServerProtocolHandler 的自动路径校验）
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req); // 本地变量
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ctx.channel().attr(ATTR_HANDSHAKER).set(handshaker); // 保存到属性
            handshaker.handshake(ctx.channel(), req);
            log.info("[CHAT][HANDSHAKE-SUCCESS] userId={} ip={} path={} firstConnection={}", userId, ctx.channel().remoteAddress(), properties.getPath(), firstConnection);
            if (firstConnection) { // 只在首个连接时广播 JOIN
                ChatMessage join = new ChatMessage();
                join.setType(MessageType.JOIN);
                join.setFromUserId(userId);
                fillSenderMeta(ctx, join);
                join.setContent("user " + userId + " joined");
                join.setIsPrivate(false);
                historyService.add(join); // 记录加入事件
                try {
                    broadcast(join, ctx.channel());
                    log.info("[CHAT][JOIN] userId={} broadcast join message", userId);
                } catch (Exception e) {
                    log.warn("Broadcast join message failed userId={}", userId, e);
                }
            }
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame closeFrame) {
            log.info("[CHAT][CLOSE] userId={} reasonCode={}", ctx.channel().attr(ATTR_USER_ID).get(), closeFrame.statusCode());
            WebSocketServerHandshaker handshaker = ctx.channel().attr(ATTR_HANDSHAKER).get();
            if (handshaker != null) handshaker.close(ctx.channel(), closeFrame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            log.debug("[CHAT][PING] userId={}", ctx.channel().attr(ATTR_USER_ID).get());
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            log.debug("[CHAT][PONG] userId={}", ctx.channel().attr(ATTR_USER_ID).get());
            return; // ignore
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            log.warn("[CHAT][UNSUPPORTED-FRAME] userId={} frameClass={}", ctx.channel().attr(ATTR_USER_ID).get(), frame.getClass().getSimpleName());
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Unsupported frame"));
            return;
        }
        String text = ((TextWebSocketFrame) frame).text();
        // 限制日志内容长度，避免刷日志
        String preview = text.length() > 200 ? text.substring(0, 200) + "..." : text;
        log.debug("[CHAT][INBOUND-RAW] userId={} rawPayload={}", ctx.channel().attr(ATTR_USER_ID).get(), preview);
        ChatMessage msg;
        try {
            msg = objectMapper.readValue(text, ChatMessage.class);
        } catch (Exception e) {
            log.warn("[CHAT][DECODE-ERROR] userId={} error={} payloadPreview={}", ctx.channel().attr(ATTR_USER_ID).get(), e.getMessage(), preview);
            ChatMessage err = new ChatMessage();
            err.setType(MessageType.ERROR);
            err.setError("INVALID_JSON");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(err)));
            return;
        }
        Long uid = ctx.channel().attr(ATTR_USER_ID).get();
        msg.setFromUserId(uid);
        fillSenderMeta(ctx, msg);
        if (msg.getType() == MessageType.PING) {
            log.trace("[CHAT][PING->PONG] userId={}", uid);
            ChatMessage pong = new ChatMessage();
            pong.setType(MessageType.PONG);
            pong.setFromUserId(0L);
            pong.setFromUsername("system");
            pong.setFromNickname("系统");
            pong.setIsPrivate(true);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(pong)));
            return;
        }
        if (msg.getType() == MessageType.CHAT || msg.getType() == MessageType.IMAGE) { // 支持文本与图片消息
            msg.setIsPrivate(msg.getToUserId() != null);
            // 使用 StpInterface 避免 SaTokenContextException（Netty 非 Web 上下文线程）
            boolean allowed;
            try {
                var perms = stpInterface.getPermissionList(uid, StpUtil.getLoginType());
                var roles = stpInterface.getRoleList(uid, StpUtil.getLoginType());
                allowed = (CollectionUtil.isNotEmpty(perms) && perms.contains("chat:send")) || (CollectionUtil.isNotEmpty(roles) && roles.contains(RoleCodeEnum.ROOT.getCode()));
            } catch (Exception ex) {
                allowed = false; // 异常时视为无权限
                log.debug("[CHAT][PERM-CHECK-ERROR] userId={} ex={}", uid, ex.getMessage());
            }
            if (!allowed) {
                ChatMessage deny = new ChatMessage();
                deny.setType(MessageType.SYSTEM);
                deny.setFromUserId(0L);
                deny.setFromUsername("系统管理员");
                deny.setFromNickname("可变羽翼核心");
                deny.setFromAvatar("http://150.158.48.185:9000/klee/20251027/e504e1ff0ade4c539996a64acf32ded8.png");
                deny.setIsPrivate(true);
                deny.setToUserId(uid);
                deny.setContent("🪽 只有『可变羽翼核心』才能发消息哦~ Netty聊天室权限受限 ๑•́ω•̀๑");
                Set<Channel> senderChannels = registry.getChannels(uid);
                String jsonDeny = toJson(deny);
                for (Channel ch : senderChannels) {
                    ch.writeAndFlush(new TextWebSocketFrame(jsonDeny));
                }
                return; // 无权限：不广播原消息 & 不记历史
            }
            // 有权限继续后续广播逻辑
            if (msg.getToUserId() != null) {
                Set<Channel> targetChannels = registry.getChannels(msg.getToUserId());
                if (targetChannels == null || targetChannels.isEmpty()) {
                    log.debug("[CHAT][PRIVATE-TARGET-OFFLINE] fromUserId={} toUserId={}", uid, msg.getToUserId());
                }
                Set<Channel> senderChannels = registry.getChannels(uid);
                broadcastTo(msg, senderChannels);
                broadcastTo(msg, targetChannels);
            } else {
                broadcast(msg, null);
            }
            historyService.add(msg); // 仅在允许时记录
        } else {
            log.debug("[CHAT][UNSUPPORTED-MSG-TYPE] type={}", msg.getType());
            ChatMessage err = new ChatMessage();
            err.setType(MessageType.ERROR);
            err.setError("UNSUPPORTED_TYPE");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(err)));
        }
    }

    private void broadcast(ChatMessage msg, Channel exclude) {
        String json = toJson(msg);
        // 使用 allChannels() 直接广播，不再调用不存在的 snapshot()
        for (Channel ch : registry.allChannels()) {
            if (exclude != null && ch == exclude) continue;
            ch.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private void broadcastTo(ChatMessage msg, Set<Channel> channels) {
        if (channels == null || channels.isEmpty()) return;
        String json = toJson(msg);
        for (Channel ch : channels) {
            ch.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private void fillSenderMeta(ChannelHandlerContext ctx, ChatMessage msg) {
        msg.setFromUsername(ctx.channel().attr(ATTR_USERNAME).get());
        msg.setFromNickname(ctx.channel().attr(ATTR_NICKNAME).get());
        msg.setFromAvatar(ctx.channel().attr(ATTR_AVATAR).get());
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }


    private String getWebSocketLocation(FullHttpRequest req) {
        String protocol = "ws";
        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + properties.getPath();
    }

    private FullHttpResponse json(HttpResponseStatus status, String msg) {
        ChatMessage m = new ChatMessage();
        m.setType(MessageType.ERROR);
        m.setError(msg);
        String body = toJson(m);
        ByteBuf buf = Unpooled.copiedBuffer(body, StandardCharsets.UTF_8);
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        return resp;
    }

    /**
     * 发送 HTTP 响应并在必要时关闭连接。
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // 若响应未设置长度，补充长度头
        if (!res.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            res.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 非正常返回 或 非 Keep-Alive 时关闭连接
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) { // 移除 throws Exception，未抛出检查异常
        Long userId = ctx.channel().attr(ATTR_USER_ID).get();
        registry.unregister(ctx.channel());
        if (userId != null && !registry.isUserOnline(userId)) { // 只有最后一个连接离线才广播 LEAVE
            ChatMessage leave = new ChatMessage();
            leave.setType(MessageType.LEAVE);
            leave.setFromUserId(userId);
            fillSenderMeta(ctx, leave);
            leave.setContent("user " + userId + " left");
            leave.setIsPrivate(false);
            historyService.add(leave);
            try {
                broadcast(leave, ctx.channel());
                log.info("[CHAT][LEAVE] userId={} disconnected (last session)", userId);
            } catch (Exception ignored) {
                log.warn("[CHAT][LEAVE-BROADCAST-FAIL] userId={}", userId);
            }
        }
    }
}
