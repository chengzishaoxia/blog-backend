package com.klee.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimit {

    /**
     * 限流 key 前缀。
     */
    String prefix();

    /**
     * 参与限流 key 计算的方法参数名。
     */
    String keyParam() default "";

    /**
     * 时间窗口（秒）。
     */
    int windowSeconds() default 60;

    /**
     * 时间窗口内允许的最大请求次数。
     */
    int maxRequests() default 1;
}