package org.example.notificationserver.service;

import jakarta.mail.internet.MimeMessage;
import org.example.notificationserver.config.NotificationEmailProperties;
import org.example.notificationserver.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void sendEmail(EmailRequest request) {
        if (!emailProperties.isEnabled()) {
            logger.info("邮件发送已关闭，跳过发送邮件");
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置发件人
            String from = request.getFrom() != null ? request.getFrom() : emailProperties.getFromAddress();
            helper.setFrom(from, emailProperties.getFromName());
            
            // 设置收件人
            helper.setTo(request.getTo().toArray(new String[0]));
            
            // 设置抄送
            if (request.getCc() != null && !request.getCc().isEmpty()) {
                helper.setCc(request.getCc().toArray(new String[0]));
            }
            
            // 设置密送
            if (request.getBcc() != null && !request.getBcc().isEmpty()) {
                helper.setBcc(request.getBcc().toArray(new String[0]));
            }
            
            helper.setSubject(request.getSubject());
            
            // 设置邮件内容
            if ("text/html".equals(request.getContentType())) {
                helper.setText(request.getContent(), true);
            } else {
                helper.setText(request.getContent(), false);
            }
            
            // 添加附件
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                for (String attachmentUrl : request.getAttachments()) {
                    // TODO: 实现附件添加逻辑
                    logger.info("添加附件: {}", attachmentUrl);
                }
            }
            
            mailSender.send(message);
            logger.info("发送邮件成功，to={}, subject={}", request.getTo(), request.getSubject());
        } catch (Exception e) {
            logger.error("发送邮件失败，to={}, subject={}", request.getTo(), request.getSubject(), e);
        }
    }
    
    private void sendHtml(String to, String subject, String html) {
        EmailRequest request = new EmailRequest();
        request.setTo(List.of(to));
        request.setSubject(subject);
        request.setContent(html);
        request.setContentType("text/html");
        sendEmail(request);
    }
}


