package com.klee.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.klee.common.result.Result;
import com.klee.common.result.ResultCode;
import com.klee.config.property.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sa-Token 全局配置
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfiguration {

    private final SecurityProperties securityProperties;
    private final StpInterface stpInterface; // 自定义权限接口 Bean 生效（保留注入以触发实现类加载）
    private final ObjectMapper objectMapper; // 用于统一 JSON 序列化
    private final RequestMappingHandlerMapping handlerMapping;

    private final Map<Method, Boolean> saIgnoreCache = new ConcurrentHashMap<>();

    /**
     * 权限策略扩展点（当前未自定义逻辑，保留占位）
     */
    @PostConstruct
    public void rewritePermissionStrategy() {
        // NO-OP: 使用 Sa-Token 默认权限校验
    }

    @Bean
    public SaServletFilter saServletFilter() {
        String[] ignore = securityProperties.getIgnoreUrls();
        String[] unsecured = securityProperties.getUnsecuredUrls();
        SaServletFilter filter = new SaServletFilter();
        filter.addInclude("/**");
        if (ignore != null) Arrays.stream(ignore).forEach(filter::addExclude);
        if (unsecured != null) Arrays.stream(unsecured).forEach(filter::addExclude);
        filter.setAuth(obj -> {
            if (isSaIgnoreRequest()) {
                return;
            }
            StpUtil.checkLogin();
        });
        filter.setError(this::buildUnauthorizedJson);
        return filter;
    }

    private boolean isSaIgnoreRequest() {
        Object source = SaHolder.getRequest().getSource();
        if (!(source instanceof HttpServletRequest request)) {
            return false;
        }
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            if (chain == null || !(chain.getHandler() instanceof HandlerMethod handlerMethod)) {
                return false;
            }
            return saIgnoreCache.computeIfAbsent(handlerMethod.getMethod(), method ->
                AnnotatedElementUtils.hasAnnotation(method, SaIgnore.class)
                    || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), SaIgnore.class)
            );
        } catch (Exception ex) {
            return false;
        }
    }

    private String buildUnauthorizedJson(Throwable e) {
        ResultCode code;
        String message;
        if (e instanceof NotLoginException nle) {
            code = switch (nle.getType()) {
                case NotLoginException.NOT_TOKEN, NotLoginException.INVALID_TOKEN, NotLoginException.TOKEN_FREEZE,
                     NotLoginException.BE_REPLACED, NotLoginException.KICK_OUT -> ResultCode.ACCESS_TOKEN_INVALID;
                default -> ResultCode.ACCESS_UNAUTHORIZED;
            };
            message = nle.getMessage();
        } else {
            code = ResultCode.ACCESS_UNAUTHORIZED;
            message = e.getMessage();
        }
        Result<Void> body = Result.failed(code, message);
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception ex) {
            String safeMsg = (message == null ? code.getMsg() : message.replace('"', ' '));
            return """
                    {"code":"%s","msg":"%s"}
                    """.formatted(body.getCode(), safeMsg);
        }
    }
}
