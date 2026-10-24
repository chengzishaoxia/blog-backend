package com.klee.config;

import com.klee.config.property.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * MailConfig 配置类，用于手动配置和注入 JavaMailSender。
 * 通过读取 MailProperties 类中配置的邮件相关属性来初始化 JavaMailSender。
 * <p>
 * 手动注入的原因是为了避免在使用 application-dev.yml 或其他非 application.yml 配置文件时，
 * IDEA 提示无法找到 JavaMailSender 的 bean。
 *
 * @author Ray
 * @since 2024/8/17
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
@Slf4j
public class MailConfig {

    private final MailProperties mailProperties;

    public MailConfig(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * 创建并配置 JavaMailSender bean。
     *
     * @return 配置好的 JavaMailSender 实例
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        if (mailProperties.getProtocol() != null && !mailProperties.getProtocol().isBlank()) {
            mailSender.setProtocol(mailProperties.getProtocol());
        }

        Properties properties = mailSender.getJavaMailProperties();
        MailProperties.Properties.Smtp smtp = mailProperties.getProperties().getSmtp();
        properties.put("mail.smtp.auth", String.valueOf(smtp.isAuth()));
        putIfNotNull(properties, "mail.smtp.connectiontimeout", smtp.getConnectionTimeout());
        putIfNotNull(properties, "mail.smtp.timeout", smtp.getTimeout());
        putIfNotNull(properties, "mail.smtp.writetimeout", smtp.getWriteTimeout());
        properties.put("mail.smtp.starttls.enable", String.valueOf(smtp.getStarttls().isEnable()));
        properties.put("mail.smtp.starttls.required", String.valueOf(smtp.getStarttls().isRequired()));
        properties.put("mail.smtp.ssl.enable", String.valueOf(smtp.getSsl().isEnable()));
        properties.put("mail.smtp.ssl.required", String.valueOf(smtp.getSsl().isRequired()));
        putIfNotBlank(properties, "mail.smtp.ssl.trust", smtp.getSsl().getTrust());
        putIfNotBlank(properties, "mail.smtp.ssl.protocols", smtp.getSsl().getProtocols());
        putIfNotNull(properties, "mail.smtp.socketFactory.port", smtp.getSocketFactory().getPort());
        putIfNotBlank(properties, "mail.smtp.socketFactory.class", smtp.getSocketFactory().getClassName());
        properties.put("mail.smtp.socketFactory.fallback", String.valueOf(smtp.getSocketFactory().isFallback()));

        log.info("JavaMailSender configured: host={}, port={}, protocol={}, smtpAuth={}, starttls={}, ssl={}",
                mailProperties.getHost(),
                mailProperties.getPort(),
                mailSender.getProtocol(),
                properties.getProperty("mail.smtp.auth"),
                properties.getProperty("mail.smtp.starttls.enable"),
                properties.getProperty("mail.smtp.ssl.enable"));

        return mailSender;
    }

    private void putIfNotNull(Properties target, String key, Object value) {
        if (value != null) {
            target.put(key, String.valueOf(value));
        }
    }

    private void putIfNotBlank(Properties target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }
}
