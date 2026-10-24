package com.klee.shared.mail.service.impl;

import com.klee.config.property.MailProperties;
import com.klee.shared.mail.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 邮件服务实现类
 *
 * @author Ray
 * @since 2024/8/17
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final MailProperties mailProperties;

    private String buildRootCauseMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        String rootMessage = rootCause.getMessage();
        if (rootMessage == null || rootMessage.isBlank()) {
            rootMessage = rootCause.getClass().getName();
        }
        return rootCause.getClass().getName() + ": " + rootMessage;
    }

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送文本邮件失败, from={}, to={}, subject={}, rootCause={}",
                    mailProperties.getFrom(), to, subject, buildRootCauseMessage(e), e);
        }
    }

    @Override
    @Async("mailTaskExecutor")
    public void sendHtmlMail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送HTML邮件失败, from={}, to={}, subject={}, rootCause={}",
                    mailProperties.getFrom(), to, subject, buildRootCauseMessage(e), e);
            throw new IllegalStateException("发送邮件失败", e);
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人地址
     * @param subject  邮件主题
     * @param text     邮件内容
     * @param filePath 附件路径
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendMailWithAttachment(String to, String subject, String text, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true表示支持HTML内容

            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("发送附件邮件失败, from={}, to={}, subject={}, filePath={}, rootCause={}",
                    mailProperties.getFrom(), to, subject, filePath, buildRootCauseMessage(e), e);
        }
    }
}
