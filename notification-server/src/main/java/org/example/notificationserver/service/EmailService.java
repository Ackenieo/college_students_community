package org.example.notificationserver.service;

import jakarta.mail.internet.MimeMessage;
import org.example.notificationserver.config.NotificationEmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationEmailProperties emailProperties;

    public void sendVerifyCodeEmail(String toAddress, String code) {
        if (!emailProperties.isEnabled()) {
            logger.info("邮件发送已关闭，跳过发送验证码邮件");
            return;
        }
        String subject = emailProperties.getSubjectVerifyCode();
        String content = "您的验证码为：" + code + "，有效期10分钟。如非本人操作请忽略。";
        sendHtml(toAddress, subject, content);
    }

    public void sendResetPasswordEmail(String toAddress, String code) {
        if (!emailProperties.isEnabled()) {
            logger.info("邮件发送已关闭，跳过发送找回密码邮件");
            return;
        }
        String subject = emailProperties.getSubjectResetPassword();
        String content = "找回密码验证码：" + code + "，有效期10分钟。如非本人操作请忽略。";
        sendHtml(toAddress, subject, content);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFromAddress(), emailProperties.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.info("发送邮件成功，to={}, subject={}", to, subject);
        } catch (Exception e) {
            logger.error("发送邮件失败，to={}, subject={}", to, subject, e);
        }
    }
}


