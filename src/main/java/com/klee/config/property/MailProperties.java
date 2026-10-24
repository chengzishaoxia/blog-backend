package com.klee.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件配置类，用于接收和存储邮件相关的配置属性。
 *
 * @author Ray
 * @since 2024/8/17
 */
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailProperties {

    /**
     * 邮件协议。
     * 例如：smtp、smtps
     */
    private String protocol;

    /**
     * 邮件服务器主机名或 IP 地址。
     * 例如：smtp.example.com
     */
    private String host;

    /**
     * 邮件服务器端口号。
     * 例如：587
     */
    private int port;

    /**
     * 用于连接邮件服务器的用户名。
     * 例如：your_email@example.com
     */
    private String username;

    /**
     * 用于连接邮件服务器的密码。
     * 该密码应安全存储，不应在代码中硬编码。
     */
    private String password;

    /**
     * 邮件发送者地址。
     */
    private String from;

    /**
     * 邮件服务器的其他属性配置。
     * 这些配置通常用于进一步定制邮件发送行为。
     */
    private Properties properties = new Properties();

    /**
     * 内部类，用于封装邮件服务器的详细配置。
     * 包含 SMTP 相关的配置选项。
     */
    @Data
    public static class Properties {

        /**
         * SMTP 配置选项类。
         * 包含认证、加密等与 SMTP 协议相关的配置。
         */
        private Smtp smtp = new Smtp();

        @Data
        public static class Smtp {

            /**
             * 是否启用 SMTP 认证。
             * 如果为 `true`，则需要提供有效的用户名和密码进行认证。
             */
            private boolean auth;

            /**
             * 建立连接超时时间（毫秒）。
             */
            private Integer connectionTimeout;

            /**
             * 读取超时时间（毫秒）。
             */
            private Integer timeout;

            /**
             * 写入超时时间（毫秒）。
             */
            private Integer writeTimeout;

            /**
             * STARTTLS 加密配置选项。
             */
            private StartTls starttls = new StartTls();

            /**
             * SSL 配置选项。
             */
            private Ssl ssl = new Ssl();

            /**
             * SocketFactory 配置选项。
             */
            private SocketFactory socketFactory = new SocketFactory();

            @Data
            public static class StartTls {

                /**
                 * 是否启用 STARTTLS 加密。
                 * 如果为 `true`，在发送邮件时将启用 STARTTLS 协议进行加密传输。
                 */
                private boolean enable;

                /**
                 * 是否强制要求服务端支持 STARTTLS。
                 */
                private boolean required;
            }

            @Data
            public static class Ssl {

                /**
                 * 是否启用 SSL。
                 */
                private boolean enable;

                /**
                 * 是否强制要求 SSL。
                 */
                private boolean required;

                /**
                 * SSL 信任主机。
                 */
                private String trust;

                /**
                 * SSL 协议版本。
                 */
                private String protocols;
            }

            @Data
            public static class SocketFactory {

                /**
                 * SocketFactory 端口。
                 */
                private Integer port;

                /**
                 * SocketFactory 实现类。
                 */
                private String className;

                /**
                 * 是否允许回退到普通 Socket。
                 */
                private boolean fallback;
            }
        }
    }
}
