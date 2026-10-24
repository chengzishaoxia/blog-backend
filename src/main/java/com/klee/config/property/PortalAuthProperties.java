package com.klee.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.portal-auth")
public class PortalAuthProperties {

    private String oauthSuccessRedirectUrl = "http://localhost:5173/auth/callback";

    private String oauthFailureRedirectUrl = "http://localhost:5173/auth/callback";

    private Qq qq = new Qq();

    private Gitee gitee = new Gitee();

    private Github github = new Github();

    private Email email = new Email();

    @Data
    public static class Qq {
        private boolean enabled;
        private String appId;
        private String appSecret;
        private String redirectUrl;
    }

    @Data
    public static class Gitee {
        private boolean enabled;
        private String appId;
        private String appSecret;
        private String redirectUrl;
    }

    @Data
    public static class Github {
        private boolean enabled;
        private String appId;
        private String appSecret;
        private String redirectUrl;
    }

    @Data
    public static class Email {
        private String siteName = "可变羽翼核心";
        private String siteUrl = "https://gitee.com/variable-wing-core/klee-blog";
        private int expireMinutes = 5;
        private String subject = "可变羽翼核心登录验证码";
        private String htmlTemplate = "<div style=\"max-width:560px;margin:0 auto;padding:32px 24px;font-family:Segoe UI,PingFang SC,Hiragino Sans GB,Microsoft YaHei,sans-serif;color:#1f2937;line-height:1.7\"><h2 style=\"margin:0 0 12px;font-size:24px;color:#111827\">${siteName}</h2><p style=\"margin:0 0 16px\">${email}，您好。</p><p style=\"margin:0 0 16px\">您的登录验证码如下，请在 ${expireMinutes} 分钟内完成验证：</p><div style=\"margin:24px 0;padding:18px 24px;border-radius:16px;background:#f3f4f6;text-align:center;font-size:32px;font-weight:700;letter-spacing:8px;color:#111827\">${code}</div><p style=\"margin:0 0 12px;color:#6b7280\">如果这不是您的操作，请直接忽略本邮件。</p><p style=\"margin:0;color:#6b7280\">站点地址：<a href=\"${siteUrl}\" style=\"color:#2563eb;text-decoration:none\">${siteUrl}</a></p></div>";
    }
}