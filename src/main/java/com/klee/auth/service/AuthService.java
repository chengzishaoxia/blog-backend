package com.klee.auth.service;

import com.klee.auth.model.CaptchaInfo;
import com.klee.auth.model.dto.EmailCodeLoginDTO;
import com.klee.auth.model.dto.EmailLoginDTO;
import com.klee.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.klee.core.security.model.AuthenticationToken;
import com.klee.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.klee.auth.model.dto.RegisterFormData; // 新增
import jakarta.servlet.http.HttpServletResponse;
import me.zhyd.oauth.model.AuthCallback;

/**
 * 认证服务接口
 *
 * @author Ray.Hao
 * @since 2.4.0
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param captchaKey 验证码唯一 key（从 /captcha 获取）
     * @param captchaCode 用户输入验证码文本
     * @return 登录结果
     */
    AuthenticationToken login(String username, String password, String captchaKey, String captchaCode, String systemType);

    /**
     * 邮箱验证码登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    AuthenticationToken loginByEmail(EmailLoginDTO loginDTO);

    AuthenticationToken loginByEmailCode(EmailCodeLoginDTO loginDTO);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    CaptchaInfo getCaptcha();

    /**
     * 刷新令牌
     *
     * @return 登录结果
     */
    AuthenticationToken refreshToken();

    /**
     * 微信小程序登录
     *
     * @param code 微信登录code
     * @return 登录结果
     */
    AuthenticationToken loginByWechat(String code);

    /**
     * 微信小程序Code登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthenticationToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO);

    /**
     * 微信小程序手机号登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthenticationToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO);

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    void sendSmsLoginCode(String mobile);

    /**
     * 发送邮箱登录验证码
     *
     * @param email 邮箱地址
     */
    void sendEmailLoginCode(String email);

    void sendEmailRegisterCode(String email);

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 登录结果
     */
    AuthenticationToken loginBySms(String mobile, String code);

    /**
     * 用户注册并自动登录
     * @param form 注册表单
     * @return 访问令牌，与登录返回一致
     */
    AuthenticationToken register(RegisterFormData form);

    /**
     * 获取第三方授权地址
     *
     * @param source 平台标识
     * @return 授权地址
     */
    String getSocialAuthorizeUrl(String source);

    /**
     * 处理第三方登录回调
     *
     * @param source 平台标识
     * @param callback 第三方回调参数
     * @param response HTTP 响应
     */
    void handleSocialCallback(String source, AuthCallback callback, HttpServletResponse response) throws java.io.IOException;
}
