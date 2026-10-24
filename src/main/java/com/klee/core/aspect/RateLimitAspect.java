package com.klee.core.aspect;

import cn.hutool.core.util.StrUtil;
import com.klee.common.annotation.RateLimit;
import com.klee.common.constant.RedisConstants;
import com.klee.common.exception.BusinessException;
import com.klee.common.result.ResultCode;
import com.klee.common.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(rateLimit)")
    public void rateLimitPointCut(RateLimit rateLimit) {
    }

    @Around(value = "rateLimitPointCut(rateLimit)", argNames = "pjp,rateLimit")
    public Object handleRateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String key = buildRateLimitKey(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), rateLimit);

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "限流服务异常");
        }
        if (count == 1) {
            redisTemplate.expire(key, rateLimit.windowSeconds(), TimeUnit.SECONDS);
        }
        if (count > rateLimit.maxRequests()) {
            throw new BusinessException(
                    ResultCode.SYSTEM_RATE_LIMITING,
                    StrUtil.format("请求过于频繁，请在{}秒后重试", rateLimit.windowSeconds())
            );
        }
        return pjp.proceed();
    }

    private String buildRateLimitKey(HttpServletRequest request, MethodSignature signature, Object[] args, RateLimit rateLimit) {
        String ip = IPUtils.getIpAddr(request);
        String keyPart = resolveKeyPart(signature, args, rateLimit.keyParam());
        return StrUtil.format(RedisConstants.RateLimiter.API, rateLimit.prefix(), ip, keyPart);
    }

    private String resolveKeyPart(MethodSignature signature, Object[] args, String keyParam) {
        if (StrUtil.isBlank(keyParam)) {
            return "default";
        }
        String[] parameterNames = signature.getParameterNames();
        for (int index = 0; index < parameterNames.length; index++) {
            if (keyParam.equals(parameterNames[index])) {
                Object arg = args[index];
                return arg == null ? "null" : StrUtil.trim(arg.toString());
            }
        }
        return "default";
    }
}