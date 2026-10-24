package com.klee.core.security.exception;

/**
 * 验证码校验异常 (Sa-Token 迁移后)
 */
public class CaptchaValidationException extends RuntimeException {
    public CaptchaValidationException(String msg) {
        super(msg);
    }
}
