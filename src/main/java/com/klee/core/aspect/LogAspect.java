package com.klee.core.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.HttpMethod;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.constant.RedisConstants;
import com.klee.common.util.IPUtils;
import com.klee.core.security.util.SecurityUtils;
import com.klee.system.model.entity.Log;
import com.klee.system.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 日志切面
 *
 * @author Ray.Hao
 * @since 2024/6/25
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final LogService logService;
    private final HttpServletRequest request;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.klee.common.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object doAround(ProceedingJoinPoint joinPoint, com.klee.common.annotation.Log logAnnotation) throws Throwable {
        // 在方法执行前获取用户ID，避免在方法执行过程中清除上下文导致获取不到用户ID
        Long userId = SecurityUtils.getUserId();
        if (Objects.isNull(userId)) {
            userId = -1L;
        }
        TimeInterval timer = DateUtil.timer();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = timer.interval(); // 执行时长
            this.saveLog(joinPoint, exception, result, logAnnotation, executionTime, userId);
        }
        return result;
    }


    /**
     * 保存日志
     *
     * @param joinPoint     切点
     * @param e             异常
     * @param jsonResult    响应结果
     * @param logAnnotation 日志注解
     * @param userId        用户ID
     */
    private void saveLog(final JoinPoint joinPoint, final Exception e, Object jsonResult, com.klee.common.annotation.Log logAnnotation, long executionTime, Long userId) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String requestParams = resolveRequestParams(joinPoint);
        // 创建日志记录
        Log log = new Log();
        log.setExecutionTime(executionTime);
        if (logAnnotation == null && e != null) {
            log.setModule(LogModuleEnum.EXCEPTION);
            log.setContent("系统发生异常");
            this.setRequestParameters(log, requestMethod, requestParams);
            log.setResponseContent(JSONUtil.toJsonStr(e.getStackTrace()));
        } else {
            log.setModule(logAnnotation.module());
            log.setContent(logAnnotation.value());
            // 请求参数
            if (logAnnotation.params()) {
                this.setRequestParameters(log, requestMethod, requestParams);
            }
            // 响应结果
            if (logAnnotation.result() && jsonResult != null) {
                log.setResponseContent(JSONUtil.toJsonStr(jsonResult));
            }
        }
        log.setRequestUri(requestURI);
        log.setCreateBy(userId);
        String ipAddr = IPUtils.getIpAddr(request);
        if (StrUtil.isNotBlank(ipAddr)) {
            log.setIp(ipAddr);
            String region = IPUtils.getRegion(ipAddr);
            // 中国|0|四川省|成都市|电信 解析省和市
            if (StrUtil.isNotBlank(region)) {
                String[] regionArray = region.split("\\|");
                if (regionArray.length > 2) {
                    log.setProvince(regionArray[2]);
                    log.setCity(regionArray[3]);
                }
            }
        }

        if (!shouldSaveLog(log.getIp(), requestURI, requestMethod, requestParams)) {
            return;
        }


        // 获取浏览器和终端系统信息
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = resolveUserAgent(userAgentString);
        if (Objects.nonNull(userAgent)) {
            // 系统信息
            log.setOs(userAgent.getOs().getName());
            // 浏览器信息
            log.setBrowser(userAgent.getBrowser().getName());
            log.setBrowserVersion(userAgent.getBrowser().getVersion(userAgentString));
        }
        // 保存日志到数据库
        logService.save(log);
    }

    /**
     * 设置请求参数到日志对象中
     *
     * @param log       操作日志
     */
    private void setRequestParameters(Log log, String requestMethod, String requestParams) {
        log.setRequestMethod(requestMethod);
        log.setRequestParams(StrUtil.sub(requestParams, 0, 65535));
    }

    /**
     * 解析请求参数字符串
     *
     * @param joinPoint 切点
     * @return 请求参数字符串
     */
    private String resolveRequestParams(JoinPoint joinPoint) {
        String requestMethod = request.getMethod();
        if (HttpMethod.GET.name().equalsIgnoreCase(requestMethod) || HttpMethod.PUT.name().equalsIgnoreCase(requestMethod) || HttpMethod.POST.name().equalsIgnoreCase(requestMethod)) {
            return convertArgumentsToString(joinPoint.getArgs());
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Map<?, ?> paramsMap = (Map<?, ?>) attributes.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            return Objects.nonNull(paramsMap) ? paramsMap.toString() : "";
        }
        return "";
    }

    /**
     * 将参数数组转换为字符串
     *
     * @param paramsArray 参数数组
     * @return 参数字符串
     */
    private String convertArgumentsToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object param : paramsArray) {
                if (!shouldFilterObject(param)) {
                    params.append(JSONUtil.toJsonStr(param)).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param obj 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    private boolean shouldFilterObject(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) obj;
            return collection.stream().anyMatch(item -> item instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return map.values().stream().anyMatch(value -> value instanceof MultipartFile);
        }
        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse;
    }


    /**
     * 解析UserAgent
     *
     * @param userAgentString UserAgent字符串
     * @return UserAgent
     */
    public UserAgent resolveUserAgent(String userAgentString) {
        if (StrUtil.isBlank(userAgentString)) {
            return null;
        }
        // 给userAgentStringMD5加密一次防止过长
        String userAgentStringMD5 = DigestUtil.md5Hex(userAgentString);
        //判断是否命中缓存
        UserAgent userAgent = Objects.requireNonNull(cacheManager.getCache("userAgent")).get(userAgentStringMD5, UserAgent.class);
        if (userAgent != null) {
            return userAgent;
        }
        userAgent = UserAgentUtil.parse(userAgentString);
        Objects.requireNonNull(cacheManager.getCache("userAgent")).put(userAgentStringMD5, userAgent);
        return userAgent;
    }

    /**
     * 同 IP、同接口、同入参在当天内只记录一次日志
     *
     * @param ip           IP 地址
     * @param requestUri   请求路径
     * @param requestMethod 请求方法
     * @param requestParams 请求参数
     * @return true 表示需要保存日志
     */
    private boolean shouldSaveLog(String ip, String requestUri, String requestMethod, String requestParams) {
        String ipKey = StrUtil.blankToDefault(ip, "unknown");
        String paramsHash = DigestUtil.md5Hex(StrUtil.blankToDefault(requestParams, ""));
        String requestIdentifier = StrUtil.join(":", requestMethod, requestUri);
        String deduplicationKey = StrUtil.format(
                RedisConstants.Lock.LOG_DEDUPLICATION,
                LocalDate.now(),
                ipKey,
                requestIdentifier,
                paramsHash
        );
        long ttlSeconds = Math.max(Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds(), 1L);
        try {
            Boolean firstSeen = redisTemplate.opsForValue().setIfAbsent(deduplicationKey, 1, ttlSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(firstSeen);
        } catch (Exception ex) {
            log.warn("Log deduplication failed, fallback to save log. key={}", deduplicationKey, ex);
            return true;
        }
    }

}
