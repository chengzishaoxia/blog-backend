package com.klee.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.klee.auth.model.CaptchaInfo;
import com.klee.auth.model.dto.EmailCodeLoginDTO;
import com.klee.auth.model.dto.EmailLoginDTO;
import com.klee.auth.model.dto.RegisterFormData;
import com.klee.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.klee.common.annotation.RateLimit;
import com.klee.common.constant.AuthConstants;
import com.klee.common.enums.LogModuleEnum;
import com.klee.common.result.Result;
import com.klee.auth.service.AuthService;
import com.klee.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.klee.core.security.model.AuthenticationToken;
import com.klee.common.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制层
 */
@Tag(name = "01.认证中心")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    @SaIgnore
    public Result<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = authService.getCaptcha();
        return Result.success(captcha);
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    @SaIgnore
    @Log(value = "登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> login(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password,
            @Parameter(description = "验证码KEY", example = "uuid") @RequestParam(required = false) String captchaKey,
            @Parameter(description = "验证码文本", example = "a1b2c") @RequestParam(required = false) String captchaCode,
            @Parameter(description = "登录系统类型", example = "admin|portal") @RequestParam(required = false) String systemType
    ) {
        AuthenticationToken authenticationToken = authService.login(username, password, captchaKey, captchaCode, systemType);
        return Result.success(authenticationToken);
    }

    @Operation(summary = "邮箱密码登录")
    @PostMapping("/login/email")
    @SaIgnore
    @Log(value = "邮箱密码登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginByEmail(@RequestBody @Valid EmailLoginDTO loginDTO) {
        AuthenticationToken loginResult = authService.loginByEmail(loginDTO);
        return Result.success(loginResult);
    }

    @Operation(summary = "邮箱验证码登录")
    @PostMapping("/login/email/code")
    @SaIgnore
    @Log(value = "邮箱验证码登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginByEmailCode(@RequestBody @Valid EmailCodeLoginDTO loginDTO) {
        AuthenticationToken loginResult = authService.loginByEmailCode(loginDTO);
        return Result.success(loginResult);
    }

    @Operation(summary = "发送邮箱登录验证码")
    @PostMapping("/email/code")
    @SaIgnore
    @RateLimit(
            prefix = AuthConstants.RateLimit.EMAIL_LOGIN_CODE_PREFIX,
            keyParam = AuthConstants.RateLimit.EMAIL_PARAM
    )
    @Log(value = "发送邮箱登录验证码", module = LogModuleEnum.USER)
    public Result<Void> sendEmailLoginCode(
            @Parameter(description = "邮箱地址", example = "demo@example.com") @RequestParam String email
    ) {
        authService.sendEmailLoginCode(email);
        return Result.success();
    }

    @Operation(summary = "发送邮箱注册验证码")
    @PostMapping("/email/register/code")
    @SaIgnore
    @RateLimit(
            prefix = AuthConstants.RateLimit.EMAIL_REGISTER_CODE_PREFIX,
            keyParam = AuthConstants.RateLimit.EMAIL_PARAM
    )
    @Log(value = "发送邮箱注册验证码", module = LogModuleEnum.USER)
    public Result<Void> sendEmailRegisterCode(
            @Parameter(description = "邮箱地址", example = "demo@example.com") @RequestParam String email
    ) {
        authService.sendEmailRegisterCode(email);
        return Result.success();
    }

    @Operation(summary = "短信验证码登录")
    @PostMapping("/login/sms")
    @SaIgnore
    @Log(value = "短信验证码登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginBySms(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile,
            @Parameter(description = "验证码", example = "1234") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginBySms(mobile, code);
        return Result.success(loginResult);
    }

    @Operation(summary = "发送登录短信验证码")
    @PostMapping("/sms/code")
    public Result<Void> sendLoginVerifyCode(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile
    ) {
        authService.sendSmsLoginCode(mobile);
        return Result.success();
    }

    @Operation(summary = "微信授权登录(Web)")
    @PostMapping("/login/wechat")
    @SaIgnore
    @Log(value = "微信登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginByWechat(
            @Parameter(description = "微信授权码", example = "code") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginByWechat(code);
        return Result.success(loginResult);
    }

    @Operation(summary = "获取第三方授权地址")
    @GetMapping("/social/render/{source}")
    @SaIgnore
    public Result<String> getSocialAuthorizeUrl(@PathVariable String source) {
        return Result.success(authService.getSocialAuthorizeUrl(source));
    }

    @Operation(summary = "第三方登录回调")
    @GetMapping("/social/callback/{source}")
    @SaIgnore
    public void handleSocialCallback(@PathVariable String source, AuthCallback callback, HttpServletResponse response) throws java.io.IOException {
        authService.handleSocialCallback(source, callback, response);
    }

    @Operation(summary = "微信小程序登录(Code)")
    @PostMapping("/wx/miniapp/code-login")
    @SaIgnore
    public Result<AuthenticationToken> loginByWxMiniAppCode(@RequestBody @Valid WxMiniAppCodeLoginDTO loginDTO) {
        AuthenticationToken token = authService.loginByWxMiniAppCode(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "微信小程序登录(手机号)")
    @PostMapping("/wx/miniapp/phone-login")
    public Result<AuthenticationToken> loginByWxMiniAppPhone(@RequestBody @Valid WxMiniAppPhoneLoginDTO loginDTO) {
        AuthenticationToken token = authService.loginByWxMiniAppPhone(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @SaIgnore
    @Log(value = "注册", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> register(@RequestBody @Valid RegisterFormData form) {
        AuthenticationToken token = authService.register(form);
        return Result.success(token);
    }

    @Operation(summary = "退出登录")
    @DeleteMapping("/logout")
    @SaIgnore
    @Log(value = "退出登录", module = LogModuleEnum.LOGIN)
    public Result<?> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh-token")
    @SaIgnore
    public Result<AuthenticationToken> refreshToken() {
        AuthenticationToken authenticationToken = authService.refreshToken();
        return Result.success(authenticationToken);
    }
}
