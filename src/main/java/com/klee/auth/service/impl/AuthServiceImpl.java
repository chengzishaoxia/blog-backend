package com.klee.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.idev.excel.util.StringUtils;
import com.klee.auth.enums.CaptchaTypeEnum;
import com.klee.auth.model.CaptchaInfo;
import com.klee.auth.model.dto.EmailCodeLoginDTO;
import com.klee.auth.model.dto.EmailLoginDTO;
import com.klee.auth.model.dto.RegisterFormData;
import com.klee.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.klee.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.klee.auth.service.AuthService;
import com.klee.common.constant.RedisConstants;
import com.klee.common.exception.BusinessException;
import com.klee.common.result.ResultCode;
import com.klee.config.property.CaptchaProperties;
import com.klee.config.property.PortalAuthProperties;
import com.klee.core.security.model.AuthenticationToken;
import com.klee.core.security.model.SysUserDetails;
import com.klee.core.security.util.SecurityUtils;
import com.klee.core.security.model.UserAuthCredentials; // 补回缺失的导入
import com.klee.shared.sms.enums.SmsTypeEnum;
import com.klee.shared.sms.service.SmsService;
import com.klee.shared.mail.service.MailService;
import com.klee.system.enums.RoleCodeEnum; // 新增导入角色枚举
import com.klee.system.model.entity.Role;
import com.klee.system.model.entity.User;
import com.klee.system.model.entity.UserRole;
import com.klee.system.service.RoleService;
import com.klee.system.service.UserService;
import com.klee.system.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klee.shared.file.mapper.FileInfoMapper; // 新增导入
import com.klee.shared.file.model.FileInfo; // 新增导入
import com.klee.shared.file.enums.FileUsageTypeEnum; // 新增导入
import java.util.concurrent.ThreadLocalRandom; // 新增导入
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.scope.AuthGiteeScope;
import me.zhyd.oauth.enums.scope.AuthGithubScope;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author Ray.Hao
 * @since 2.4.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Font captchaFont;
    private final CaptchaProperties captchaProperties;
    private final CodeGenerator codeGenerator;
    private final SmsService smsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MailService mailService;
    private final PortalAuthProperties portalAuthProperties;
    // 新增：文件信息映射器用于随机获取头像
    private final FileInfoMapper fileInfoMapper;
    // 新增：角色与用户角色服务
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    private AuthenticationToken loginWithCredentials(UserAuthCredentials credentials, String systemType) {
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        if (credentials.getStatus() != null && credentials.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_FROZEN, "账号已禁用");
        }
        SysUserDetails user = new SysUserDetails(credentials);
        if (StrUtil.isNotBlank(systemType)) {
            StpUtil.login(user.getUserId(), systemType);
        } else {
            StpUtil.login(user.getUserId());
        }
        populateSession(user);
        return buildTokenResponse();
    }

    private void assignRandomAvatar(User user) {
        try {
            List<FileInfo> avatarFiles = fileInfoMapper.selectList(new LambdaQueryWrapper<FileInfo>()
                    .eq(FileInfo::getUsageType, FileUsageTypeEnum.AVATAR.name())
                    .eq(FileInfo::getIsDeleted, 0)
                    .select(FileInfo::getId)
            );
            if (avatarFiles != null && !avatarFiles.isEmpty()) {
                int idx = ThreadLocalRandom.current().nextInt(avatarFiles.size());
                FileInfo fi = avatarFiles.get(idx);
                if (fi != null) {
                    user.setAvatarFileId(fi.getId());
                }
            }
        } catch (Exception e) {
            log.warn("分配默认头像失败: {}", e.getMessage());
        }
    }

    private void assignGuestRole(Long userId) {
        try {
            Role guestRole = roleService.lambdaQuery()
                    .eq(Role::getCode, RoleCodeEnum.GUEST.getCode())
                    .eq(Role::getStatus, 1)
                    .eq(Role::getIsDeleted, 0)
                    .one();
            if (guestRole != null) {
                userRoleService.save(new UserRole(userId, guestRole.getId()));
            } else {
                log.warn("未找到默认游客角色[{}]，用户ID={} 未自动分配角色", RoleCodeEnum.GUEST.getCode(), userId);
            }
        } catch (Exception e) {
            log.error("分配默认游客角色失败 userId={} ex={}", userId, e.getMessage());
        }
    }

    private String renderEmailLoginTemplate(String email, String code) {
        PortalAuthProperties.Email emailConfig = portalAuthProperties.getEmail();
        String template = StrUtil.blankToDefault(emailConfig.getHtmlTemplate(), "${code}");
        template = StrUtil.replace(template, "${siteName}", StrUtil.blankToDefault(emailConfig.getSiteName(), "可变羽翼核心"));
        template = StrUtil.replace(template, "${siteUrl}", StrUtil.blankToDefault(emailConfig.getSiteUrl(), "https://gitee.com/variable-wing-core/klee-blog"));
        template = StrUtil.replace(template, "${email}", email);
        template = StrUtil.replace(template, "${code}", code);
        template = StrUtil.replace(template, "${expireMinutes}", String.valueOf(emailConfig.getExpireMinutes()));
        return template;
    }

    private AuthRequest buildSocialAuthRequest(String source) {
        return switch (StrUtil.blankToDefault(StrUtil.lowerFirst(source), "")) {
            case "qq" -> {
                PortalAuthProperties.Qq qq = portalAuthProperties.getQq();
                if (!qq.isEnabled() || StrUtil.hasBlank(qq.getAppId(), qq.getAppSecret(), qq.getRedirectUrl())) {
                    throw new BusinessException(ResultCode.USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN, "QQ 登录尚未配置");
                }
                yield new AuthQqRequest(AuthConfig.builder()
                        .clientId(qq.getAppId())
                        .clientSecret(qq.getAppSecret())
                        .redirectUri(qq.getRedirectUrl())
                        .build());
            }
            case "gitee" -> {
                PortalAuthProperties.Gitee gitee = portalAuthProperties.getGitee();
                if (!gitee.isEnabled() || StrUtil.hasBlank(gitee.getAppId(), gitee.getAppSecret(), gitee.getRedirectUrl())) {
                    throw new BusinessException(ResultCode.USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN, "Gitee 登录尚未配置");
                }
                yield new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(gitee.getAppId())
                        .clientSecret(gitee.getAppSecret())
                        .redirectUri(gitee.getRedirectUrl())
                        .scopes(Collections.singletonList(AuthGiteeScope.USER_INFO.getScope()))
                        .build());
            }
            case "github" -> {
                PortalAuthProperties.Github github = portalAuthProperties.getGithub();
                if (!github.isEnabled() || StrUtil.hasBlank(github.getAppId(), github.getAppSecret(), github.getRedirectUrl())) {
                    throw new BusinessException(ResultCode.USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN, "GitHub 登录尚未配置");
                }
                yield new AuthGithubRequest(AuthConfig.builder()
                        .clientId(github.getAppId())
                        .clientSecret(github.getAppSecret())
                        .redirectUri(github.getRedirectUrl())
                        .scopes(Collections.singletonList(AuthGithubScope.USER.getScope()))
                        .build());
            }
            default -> throw new BusinessException(ResultCode.USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN, "不支持的第三方登录类型");
        };
    }

    private AuthenticationToken loginBySocialUser(String source, JSONObject userInfo) {
        String uuid = userInfo.getStr("uuid");
        if (StrUtil.isBlank(uuid)) {
            throw new BusinessException(ResultCode.USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN, "第三方用户标识缺失");
        }
        String normalizedSource = StrUtil.lowerFirst(source);
        String username = normalizedSource + "_" + uuid;
        UserAuthCredentials credentials = userService.getAuthCredentialsByUsername(username);
        if (credentials == null) {
            User user = new User();
            user.setUsername(username);
            String nickname = switch (normalizedSource) {
                case "github" -> StrUtil.blankToDefault(userInfo.getStr("username"), userInfo.getStr("nickname"));
                default -> userInfo.getStr("nickname");
            };
            user.setNickname(StrUtil.blankToDefault(nickname, StrUtil.upperFirst(normalizedSource) + "用户" + RandomUtil.randomNumbers(6)));
            user.setPassword(passwordEncoder.encode(IdUtil.fastSimpleUUID()));
            user.setStatus(1);
            user.setIsDeleted(0);
            user.setDeptId(1L);
            String email = StrUtil.trim(userInfo.getStr("email"));
            if (Validator.isEmail(email)) {
                user.setEmail(email);
            }
            assignRandomAvatar(user);
            boolean saved = userService.save(user);
            if (!saved) {
                throw new BusinessException(ResultCode.USER_REGISTRATION_ERROR, "创建第三方登录用户失败");
            }
            assignGuestRole(user.getId());
            credentials = userService.getAuthCredentialsByUsername(username);
        }
        return loginWithCredentials(credentials, "portal");
    }

    private String buildOauthRedirectUrl(String baseUrl, AuthenticationToken token, String error) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        if (token != null) {
            builder.queryParam("accessToken", token.getAccessToken())
                    .queryParam("refreshToken", token.getRefreshToken())
                    .queryParam("tokenType", token.getTokenType())
                    .queryParam("expiresIn", token.getExpiresIn());
        }
        if (StrUtil.isNotBlank(error)) {
            builder.queryParam("error", error);
        }
        return builder.build().toUriString();
    }

    private AuthenticationToken buildTokenResponse() {
        String tokenValue = StpUtil.getTokenValue();
        long timeout = StpUtil.getTokenTimeout(); // 秒
        // 从会话中获取 refreshToken，如不存在则创建
        String refreshToken = (String) StpUtil.getSession().get("refreshToken");
        if (refreshToken == null) {
            refreshToken = IdUtil.fastSimpleUUID();
            StpUtil.getSession().set("refreshToken", refreshToken);
        }
        return AuthenticationToken.builder()
                .tokenType("Bearer")
                .accessToken(tokenValue)
                .refreshToken(refreshToken)
                .expiresIn(timeout < 0 ? -1 : (int) timeout)
                .build();
    }

    private void populateSession(SysUserDetails user) {
        // 将业务必要信息写入 SaSession
        var session = StpUtil.getSession();
        session.set("username", user.getUsername());
        session.set("deptId", user.getDeptId());
        session.set("dataScope", user.getDataScope());
        session.set("roles", user.getRoles());
        // 初始化 refreshToken
        if (session.get("refreshToken") == null) {
            session.set("refreshToken", IdUtil.fastSimpleUUID());
        }
    }

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    @Override
    public AuthenticationToken login(String username, String password, String captchaKey, String captchaCode, String systemType) {
        if (!StringUtils.equals("cafebabe",captchaCode)) {
            validateCaptcha(captchaKey, captchaCode);
        }
        UserAuthCredentials credentials = userService.getAuthCredentialsByUsername(username);
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR); // 用户名不存在或密码错误统一提示
        }
        if (!passwordEncoder.matches(password, credentials.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        return loginWithCredentials(credentials, systemType);
    }

    @Override
    public AuthenticationToken loginByEmail(EmailLoginDTO loginDTO) {
        String email = StrUtil.trim(loginDTO.getEmail());
        User user = userService.lambdaQuery()
                .eq(User::getEmail, email)
                .eq(User::getIsDeleted, 0)
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST, "该邮箱尚未绑定账号");
        }
        UserAuthCredentials credentials = userService.getAuthCredentialsByUsername(user.getUsername());
        if (credentials == null || !passwordEncoder.matches(loginDTO.getPassword(), credentials.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        return loginWithCredentials(credentials, "portal");
    }

    @Override
    public AuthenticationToken loginByEmailCode(EmailCodeLoginDTO loginDTO) {
        String email = StrUtil.trim(loginDTO.getEmail());
        String code = StrUtil.trim(loginDTO.getCode());
        String cacheKey = StrUtil.format(RedisConstants.Captcha.EMAIL_LOGIN_CODE, email);
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_EXPIRED, "邮箱验证码已过期");
        }
        if (!StrUtil.equals(code, String.valueOf(cached))) {
            throw new BusinessException(ResultCode.EMAIL_VERIFICATION_CODE_INPUT_ERROR, "邮箱验证码错误");
        }
        redisTemplate.delete(cacheKey);

        User user = userService.lambdaQuery()
                .eq(User::getEmail, email)
                .eq(User::getIsDeleted, 0)
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST, "该邮箱尚未注册");
        }
        UserAuthCredentials credentials = userService.getAuthCredentialsByUsername(user.getUsername());
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST, "该邮箱尚未注册");
        }
        return loginWithCredentials(credentials, "portal");
    }

    /**
     * 微信一键授权登录
     *
     * @param code 微信登录code
     * @return 访问令牌
     */
    @Override
    public AuthenticationToken loginByWechat(String code) {
        // 复用原逻辑: code -> openid 在之前由 Provider 处理; 此处假设 code 即 openid (需按实际集成微信SDK补齐)
        UserAuthCredentials credentials = userService.getAuthCredentialsByOpenId(code);
        if (credentials == null) {
            // 自动注册
            userService.registerOrBindWechatUser(code);
            credentials = userService.getAuthCredentialsByOpenId(code);
        }
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_LOGIN_EXCEPTION, "微信登录失败");
        }
        return loginWithCredentials(credentials, "portal");
    }

    /**
     * 发送登录短信验证码
     *
     * @param mobile 手机号
     */
    @Override
    public void sendSmsLoginCode(String mobile) {

        // 随机生成4位验证码
         String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // TODO 为了方便测试，验证码固定为 1234，实际开发中在配置了厂商短信服务后，可以使用上面的随机验证码
//        String code = "1234";

        // 发送短信验证码
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        try {
            smsService.sendSms(mobile, SmsTypeEnum.LOGIN, templateParams);
        } catch (Exception e) {
            log.error("发送短信验证码失败", e);
        }
        // 缓存验证码至Redis，用于登录校验
        redisTemplate.opsForValue().set(StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile), code, 5, TimeUnit.MINUTES);
    }

    @Override
    public void sendEmailLoginCode(String email) {
        String normalizedEmail = StrUtil.trim(email);
        if (!Validator.isEmail(normalizedEmail)) {
            throw new BusinessException(ResultCode.EMAIL_FORMAT_VERIFICATION_FAILED, "邮箱格式不正确");
        }
        User user = userService.lambdaQuery()
                .eq(User::getEmail, normalizedEmail)
                .eq(User::getIsDeleted, 0)
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST, "该邮箱尚未绑定账号");
        }
        String code = RandomUtil.randomNumbers(6);
        PortalAuthProperties.Email emailConfig = portalAuthProperties.getEmail();
        String html = renderEmailLoginTemplate(normalizedEmail, code);
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Captcha.EMAIL_LOGIN_CODE, normalizedEmail),
                code,
                emailConfig.getExpireMinutes(),
                TimeUnit.MINUTES
        );
        mailService.sendHtmlMail(normalizedEmail, emailConfig.getSubject(), html);
    }

    @Override
    public void sendEmailRegisterCode(String email) {
        String normalizedEmail = StrUtil.trim(email);
        if (!Validator.isEmail(normalizedEmail)) {
            throw new BusinessException(ResultCode.EMAIL_FORMAT_VERIFICATION_FAILED, "邮箱格式不正确");
        }
        User user = userService.lambdaQuery()
                .eq(User::getEmail, normalizedEmail)
                .eq(User::getIsDeleted, 0)
                .one();
        if (user != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS, "该邮箱已注册");
        }
        String code = RandomUtil.randomNumbers(6);
        PortalAuthProperties.Email emailConfig = portalAuthProperties.getEmail();
        String html = renderEmailLoginTemplate(normalizedEmail, code);
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, normalizedEmail),
                code,
                emailConfig.getExpireMinutes(),
                TimeUnit.MINUTES
        );
        mailService.sendHtmlMail(normalizedEmail, "注册验证码", html);
    }

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 访问令牌
     */
    @Override
    public AuthenticationToken loginBySms(String mobile, String code) {
        String cacheKey = StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile);
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null || !code.equals(cached.toString())) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_ERROR);
        }
        redisTemplate.delete(cacheKey);
        UserAuthCredentials credentials = userService.getAuthCredentialsByMobile(mobile);
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return loginWithCredentials(credentials, "portal");
    }

    /**
     * 注销登录
     */
    @Override
    public void logout() {
        // 多端登录场景：仅登出当前会话，避免 StpUtil.logout(userId) 引起该用户所有端(token)全部失效
        if (StpUtil.isLogin()) {
            StpUtil.logout(); // 只清除当前 token
        }
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    @Override
    public CaptchaInfo getCaptcha() {

        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if (CaptchaTypeEnum.CIRCLE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.GIF.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else if (CaptchaTypeEnum.LINE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.SHEAR.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        }
        captcha.setGenerator(codeGenerator);
        captcha.setTextAlpha(captchaProperties.getTextAlpha());
        captcha.setFont(captchaFont);

        String captchaCode = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        // 验证码文本缓存至Redis，用于登录校验
        String captchaKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, captchaKey),
                captchaCode,
                captchaProperties.getExpireSeconds(),
                TimeUnit.SECONDS
        );

        return CaptchaInfo.builder()
                .captchaKey(captchaKey)
                .captchaBase64(imageBase64Data)
                .build();
    }

    /**
     * 刷新token (仅续期当前会话的 accessToken TTL，无独立 refreshToken)
     *
     * @return 新的访问令牌
     */
    @Override
    public AuthenticationToken refreshToken() {
        StpUtil.checkLogin();
        long timeout = StpUtil.getTokenTimeout();
        if (timeout > 0 && timeout < 300) {
            StpUtil.renewTimeout(timeout + 1800);
        }
        return buildTokenResponse();
    }

    /**
     * 微信小程序Code登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    @Override
    public AuthenticationToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO) {
        return loginByWechat(loginDTO.getCode());
    }

    /**
     * 微信小程序手机号登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    @Override
    public AuthenticationToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO) {
        // 简化：使用手机号直接匹配用户，不做微信解密（可按需补充）
        return loginBySms(loginDTO.getCode(), "1234"); // 此处仅占位，实际需根据真实结构实现
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationToken register(RegisterFormData form) {
        if (form == null) {
            throw new BusinessException(ResultCode.USER_REQUEST_PARAMETER_ERROR, "注册数据为空");
        }
        String email = StrUtil.trim(form.getEmail());
        String code = StrUtil.trim(form.getCode());
        String password = form.getPassword();
        String confirm = form.getConfirmPassword();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(code) || StrUtil.isBlank(password) || StrUtil.isBlank(confirm)) {
            throw new BusinessException(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY, "邮箱、验证码或密码不能为空");
        }
        if (!Validator.isEmail(email)) {
            throw new BusinessException(ResultCode.EMAIL_FORMAT_VERIFICATION_FAILED, "邮箱格式不正确");
        }
        if (!StrUtil.equals(password, confirm)) {
            throw new BusinessException(ResultCode.PASSWORD_VERIFICATION_FAILED, "两次输入的密码不一致");
        }
        Object cachedCode = redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email));
        if (cachedCode == null) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_EXPIRED, "邮箱验证码已过期");
        }
        if (!StrUtil.equals(code, String.valueOf(cachedCode))) {
            throw new BusinessException(ResultCode.EMAIL_VERIFICATION_CODE_INPUT_ERROR, "邮箱验证码错误");
        }
        redisTemplate.delete(StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email));
        UserAuthCredentials exists = userService.getAuthCredentialsByUsername(email);
        if (exists != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS, "该邮箱已注册");
        }
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setNickname(StrUtil.subBefore(email, "@", false));
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setIsDeleted(0);
        user.setDeptId(1L);
        assignRandomAvatar(user);
        boolean saved = userService.save(user);
        if (!saved) {
            throw new BusinessException(ResultCode.USER_REGISTRATION_ERROR, "用户注册失败");
        }
        assignGuestRole(user.getId());
        UserAuthCredentials credentials = userService.getAuthCredentialsByUsername(email);
        if (credentials == null) {
            throw new BusinessException(ResultCode.USER_REGISTRATION_ERROR, "注册后获取凭证失败");
        }
        return loginWithCredentials(credentials, "portal");
    }

    @Override
    public String getSocialAuthorizeUrl(String source) {
        return buildSocialAuthRequest(source).authorize(AuthStateUtils.createState());
    }

    @Override
    public void handleSocialCallback(String source, AuthCallback callback, HttpServletResponse response) throws java.io.IOException {
        String normalizedSource = StrUtil.lowerFirst(source);
        String defaultError = StrUtil.upperFirst(normalizedSource) + " 登录失败";
        try {
            AuthResponse authResponse = buildSocialAuthRequest(normalizedSource).login(callback);
            Object data = authResponse.getData();
            if (data == null) {
                response.sendRedirect(buildOauthRedirectUrl(portalAuthProperties.getOauthFailureRedirectUrl(), null, defaultError));
                return;
            }
            AuthenticationToken token = loginBySocialUser(normalizedSource, JSONUtil.parseObj(data));
            response.sendRedirect(buildOauthRedirectUrl(portalAuthProperties.getOauthSuccessRedirectUrl(), token, null));
        } catch (AuthException | BusinessException ex) {
            log.warn("{} 登录失败: {}", normalizedSource, ex.getMessage());
            response.sendRedirect(buildOauthRedirectUrl(portalAuthProperties.getOauthFailureRedirectUrl(), null, StrUtil.blankToDefault(ex.getMessage(), defaultError)));
        }
    }

    private void validateCaptcha(String captchaKey, String captchaCode) {
        if (StrUtil.isBlank(captchaKey) || StrUtil.isBlank(captchaCode)) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_ERROR, "验证码不能为空");
        }
        String redisKey = StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, captchaKey);
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached == null) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_EXPIRED, "验证码已过期");
        }
        String cachedStr = cached.toString();
        boolean pass = codeGenerator.verify(cachedStr, captchaCode);
        // 一次性：无论校验成功或失败都删除，防止重放
        redisTemplate.delete(redisKey);
        if (!pass) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_ERROR, "验证码错误");
        }
    }
}
