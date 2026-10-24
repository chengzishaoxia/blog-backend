package com.klee.module.chat.server.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.klee.module.chat.config.ChatServerProperties;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.realtime.RealtimeSessionRegistry;
import com.klee.shared.realtime.model.RealtimeConnectionSnapshot;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.AttributeKey;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.klee.system.model.entity.User;
import com.klee.system.service.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
@ChannelHandler.Sharable
public class RealtimeGatewayHandler extends SimpleChannelInboundHandler<Object> {

    private static final AttributeKey<WebSocketServerHandshaker> ATTR_HANDSHAKER = AttributeKey.valueOf("HANDSHAKER");
    private static final AttributeKey<SocketRouteHandler> ATTR_ROUTE_HANDLER = AttributeKey.valueOf("ROUTE_HANDLER");
    private static final long SNAPSHOT_TTL_MILLIS = 300_000L;

    private final ChatServerProperties properties;
    private final List<SocketRouteHandler> routeHandlers;
    private final RealtimeSessionRegistry sessionRegistry;
    private final UserService userService;
    private final FileInfoMapper fileInfoMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest request) {
            handleHttpRequest(ctx, request);
            return;
        }
        if (msg instanceof WebSocketFrame frame) {
            handleWebSocketFrame(ctx, frame);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess() || !"websocket".equalsIgnoreCase(req.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        URI uri = URI.create(req.uri());
        SocketRouteHandler routeHandler = routeHandlerMap().get(uri.getPath());
        if (routeHandler == null) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            return;
        }

        Long userId = resolveLoginUserId(req);
        if (userId == null) {
            sendHttpResponse(ctx, req, json(HttpResponseStatus.UNAUTHORIZED, "NOT_LOGIN"));
            return;
        }
        User user = userService.getById(userId);
        if (user == null) {
            sendHttpResponse(ctx, req, json(HttpResponseStatus.UNAUTHORIZED, "INVALID_USER"));
            return;
        }

        RealtimeConnectionSnapshot snapshot = buildSnapshot(ctx, user, routeHandler, uri.getPath());
        sessionRegistry.register(ctx.channel(), snapshot, SNAPSHOT_TTL_MILLIS);
        ctx.channel().attr(ATTR_ROUTE_HANDLER).set(routeHandler);

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req, uri.getPath()), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }
        ctx.channel().attr(ATTR_HANDSHAKER).set(handshaker);
        handshaker.handshake(ctx.channel(), req);
        routeHandler.afterConnect(ctx, snapshot);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame closeFrame) {
            WebSocketServerHandshaker handshaker = ctx.channel().attr(ATTR_HANDSHAKER).get();
            if (handshaker != null) {
                handshaker.close(ctx.channel(), closeFrame.retain());
            }
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            sessionRegistry.touch(ctx.channel(), SNAPSHOT_TTL_MILLIS);
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            sessionRegistry.touch(ctx.channel(), SNAPSHOT_TTL_MILLIS);
            return;
        }
        if (!(frame instanceof TextWebSocketFrame textFrame)) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Unsupported frame"));
            return;
        }
        sessionRegistry.touch(ctx.channel(), SNAPSHOT_TTL_MILLIS);
        SocketRouteHandler routeHandler = ctx.channel().attr(ATTR_ROUTE_HANDLER).get();
        if (routeHandler == null) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Route handler missing"));
            return;
        }
        routeHandler.handleTextMessage(ctx, textFrame.text());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        SocketRouteHandler routeHandler = ctx.channel().attr(ATTR_ROUTE_HANDLER).get();
        RealtimeConnectionSnapshot snapshot = sessionRegistry.unregister(ctx.channel());
        if (routeHandler != null && snapshot != null) {
            routeHandler.afterDisconnect(ctx, snapshot);
        }
    }

    private Long resolveLoginUserId(FullHttpRequest req) {
        String token = null;
        String tokenName = SaManager.getConfig().getTokenName();
        String cookieHeader = req.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null) {
            try {
                Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
                for (Cookie cookie : cookies) {
                    if (StringUtil.equals(tokenName, cookie.name())) {
                        token = cookie.value();
                        break;
                    }
                }
            } catch (Exception ex) {
                log.debug("解析 WebSocket Cookie 失败 ex={}", ex.getMessage());
            }
        }
        try {
            Object loginId = token != null ? StpUtil.getLoginIdByToken(token) : StpUtil.getLoginIdDefaultNull();
            if (loginId == null) {
                return null;
            }
            if (loginId instanceof Long value) {
                return value;
            }
            return Long.parseLong(loginId.toString());
        } catch (NotLoginException ex) {
            return null;
        }
    }

    private RealtimeConnectionSnapshot buildSnapshot(ChannelHandlerContext ctx, User user, SocketRouteHandler routeHandler, String path) {
        RealtimeConnectionSnapshot snapshot = new RealtimeConnectionSnapshot();
        snapshot.setConnectionId(UUID.randomUUID().toString().replace("-", ""));
        snapshot.setUserId(user.getId());
        snapshot.setUsername(user.getUsername());
        snapshot.setNickname(user.getNickname());
        snapshot.setAvatar(resolveAvatar(user));
        snapshot.setChannelType(routeHandler.channelType());
        snapshot.setPath(path);
        snapshot.setRemoteAddress(resolveRemoteAddress(ctx));
        snapshot.setConnectedAt(System.currentTimeMillis());
        snapshot.setLastSeenAt(System.currentTimeMillis());
        return snapshot;
    }

    private String resolveAvatar(User user) {
        if (user.getAvatarFileId() == null) {
            return null;
        }
        FileInfo fileInfo = fileInfoMapper.selectById(user.getAvatarFileId());
        return fileInfo == null ? null : fileInfo.getUrl();
    }

    private String resolveRemoteAddress(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress address) {
            return address.getAddress().getHostAddress() + ":" + address.getPort();
        }
        return String.valueOf(ctx.channel().remoteAddress());
    }

    private Map<String, SocketRouteHandler> routeHandlerMap() {
        return routeHandlers.stream().collect(Collectors.toMap(SocketRouteHandler::path, Function.identity()));
    }

    private String getWebSocketLocation(FullHttpRequest req, String path) {
        return "ws://" + req.headers().get(HttpHeaderNames.HOST) + path;
    }

    private FullHttpResponse json(HttpResponseStatus status, String msg) {
        ByteBuf buf = Unpooled.copiedBuffer("{\"message\":\"" + msg + "\"}", StandardCharsets.UTF_8);
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        return resp;
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (!res.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            res.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());
        }
        ChannelFuture future = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}