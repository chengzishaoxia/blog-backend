package com.klee.common.constant;

/**
 * 认证模块常量。
 */
public interface AuthConstants {

    interface RateLimit {
        String EMAIL_LOGIN_CODE_PREFIX = "auth:email:login-code";
        String EMAIL_REGISTER_CODE_PREFIX = "auth:email:register-code";
        String EMAIL_PARAM = "email";
    }
}