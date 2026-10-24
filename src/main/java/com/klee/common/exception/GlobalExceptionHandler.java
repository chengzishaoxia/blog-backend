package com.klee.common.exception;

import cn.dev33.satoken.exception.*;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.klee.common.result.Result;
import com.klee.common.result.ResultCode;
import com.klee.core.security.exception.CaptchaValidationException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 全局系统异常处理器
 * <p>
 * 调整异常处理的HTTP状态码，丰富异常处理类型
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理绑定异常
     * <p>
     * 当请求参数绑定到对象时发生错误，会抛出 BindException 异常。
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(BindException e) {
        log.error("BindException:{}", e.getMessage());
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.USER_REQUEST_PARAMETER_ERROR, msg);
    }

    /**
     * 处理 @RequestParam 参数校验异常
     * <p>
     * 当请求参数在校验过程中发生违反约束条件的异常时（如 @RequestParam 验证不通过），
     * 会捕获到 ConstraintViolationException 异常。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException:{}", e.getMessage());
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * 处理方法参数校验异常
     * <p>
     * 当使用 @Valid 或 @Validated 注解对方法参数进行验证时，如果验证失败，
     * 会抛出 MethodArgumentNotValidException 异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:{}", e.getMessage());
        String msg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * 处理接口不存在的异常
     * <p>
     * 当客户端请求一个不存在的路径时，会抛出 NoHandlerFoundException 异常。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> Result<T> processException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.INTERFACE_NOT_EXIST);
    }

    /**
     * 处理静态资源或路径未找到异常（Spring Boot 3+ 可能抛出）
     * 当某个 URL 被资源处理器匹配但未找到物理资源，或未命中任何处理器且未开启 throw-exception-if-no-handler-found
     * 时可能出现此异常。统一按 404 返回。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> Result<T> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("NoResourceFoundException: {}", e.getMessage());
        return Result.failed(ResultCode.INTERFACE_NOT_EXIST);
    }

    /**
     * 处理缺少请求参数的异常
     * <p>
     * 当请求缺少必需的参数时，会抛出 MissingServletRequestParameterException 异常。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
    }

    /**
     * 处理方法参数类型不匹配的异常
     * <p>
     * 当请求参数类型不匹配时，会抛出 MethodArgumentTypeMismatchException 异常。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.PARAMETER_FORMAT_MISMATCH, "类型错误");
    }

    /**
     * 处理 Servlet 异常
     * <p>
     * 当 Servlet 处理请求时发生异常时，会抛出 ServletException 异常。
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ServletException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 处理非法参数异常
     * <p>
     * 当方法接收到非法参数时，会抛出 IllegalArgumentException 异常。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 处理 JSON 处理异常
     * <p>
     * 当处理 JSON 数据时发生错误，会抛出 JsonProcessingException 异常。
     */
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleJsonProcessingException(JsonProcessingException e) {
        log.error("Json转换异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 处理请求体不可读的异常
     * <p>
     * 当请求体不可读时，会抛出 HttpMessageNotReadableException 异常。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String errorMessage = "请求体不可为空";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return Result.failed(errorMessage);
    }

    /**
     * 处理类型不匹配异常
     * <p>
     * 当方法参数类型不匹配时，会抛出 TypeMismatchException 异常。
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(TypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 处理 SQL 语法错误异常
     * <p>
     * 当 SQL 语法错误时，会抛出 BadSqlGrammarException 异常。
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        String errorMsg = e.getMessage();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.contains("denied to user")) {
            return Result.failed(ResultCode.DATABASE_ACCESS_DENIED);
        } else {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 处理 SQL 语法错误异常
     * <p>
     * 当 SQL 语法错误时，会抛出 SQLSyntaxErrorException 异常。
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> processSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.DATABASE_EXECUTION_SYNTAX_ERROR);
    }


    /**
     * 处理 SQL 违反了完整性约束
     * <p>
     * 当 SQL 违反了完整性约束时，会抛出 SQLIntegrityConstraintViolationException 异常。
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.INTEGRITY_CONSTRAINT_VIOLATION);
    }

    /**
     * 处理业务异常
     * <p>
     * 当业务逻辑发生错误时，会抛出 BusinessException 异常。
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleBizException(BusinessException e) {
        log.error("biz exception", e);
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode(), e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * 处理验证码异常
     * <p>
     * 当验证码校验失败时，会抛出 CaptchaValidationException 异常。
     */
    @ExceptionHandler(CaptchaValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleCaptcha(CaptchaValidationException e){
        return Result.failed(ResultCode.USER_VERIFICATION_CODE_ERROR, e.getMessage());
    }

    /**
     * 处理未登录异常
     * <p>
     * 当用户未登录时，会抛出 NotLoginException 异常。
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public <T> Result<T> handleNotLogin(NotLoginException e){
        return Result.failed(ResultCode.ACCESS_UNAUTHORIZED, e.getMessage());
    }

    /**
     * 处理无权限异常
     * <p>
     * 当用户没有访问接口的权限时，会抛出 NotPermissionException 异常。
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleNoPerm(NotPermissionException e){
        return Result.failed(ResultCode.NO_PERMISSION_TO_USE_API, e.getMessage());
    }

    /**
     * 处理无角色异常
     * <p>
     * 当用户没有角色时，会抛出 NotRoleException 异常。
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleNoRole(NotRoleException e){
        return Result.failed(ResultCode.NO_PERMISSION_TO_USE_API, e.getMessage());
    }

    /**
     * 处理账号被禁用异常
     * <p>
     * 当用户账号被禁用时，会抛出 DisableLoginException 异常。
     */
    // @ExceptionHandler(DisableLoginException.class)
    // @ResponseStatus(HttpStatus.FORBIDDEN)
    // public <T> Result<T> handleDisable(DisableLoginException e){
    //     long disableTime = e.getDisableTime();
    //     String msg = disableTime > 0 ? ("账号已被封禁，剩余: " + disableTime + "s") : e.getMessage();
    //     return Result.failed(ResultCode.ACCOUNT_FROZEN, msg);
    // }

    /**
     * 处理账号或服务被禁用异常（Sa-Token: DisableServiceException）
     */
    @ExceptionHandler(DisableServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleDisableService(DisableServiceException e){
        long disableTime = e.getDisableTime();
        String msg = disableTime > 0 ? ("账号已被封禁，剩余: " + disableTime + "s") : e.getMessage();
        return Result.failed(ResultCode.ACCOUNT_FROZEN, msg);
    }

    /**
     * 处理所有未捕获的异常
     * <p>
     * 当发生未捕获的异常时，会抛出 Exception 异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleException(Exception e){
        log.error("unknown exception", e);
        return Result.failed(e.getLocalizedMessage());
    }

    /**
     * 传参类型错误时，用于消息转换
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = "%s字段类型错误".formatted(matchString.replaceAll("\"", ""));
            group += matchString;
        }
        return group;
    }
}
