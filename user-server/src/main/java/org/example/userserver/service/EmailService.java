package org.example.userserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务类
 * 用于发送验证码邮件
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送验证码邮件
     *
     * @param toEmail   接收方邮箱
     * @param code      验证码
     * @return          发送是否成功
     */
    public boolean sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("验证码 - 大学生社区平台");
            
            // 邮件内容
            String content = String.format(
                "您好！\n\n" +
                "您的验证码是：%s\n\n" +
                "验证码有效期为5分钟，请及时使用。\n" +
                "如非本人操作，请忽略此邮件。\n\n" +
                "大学生社区平台团队", code
            );
            
            message.setText(content);
            
            mailSender.send(message);
            log.info("验证码邮件发送成功，接收方：{}，验证码：{}", toEmail, code);
            return true;
            
        } catch (Exception e) {
            log.error("验证码邮件发送失败，接收方：{}，错误信息：{}", toEmail, e.getMessage());
            e.printStackTrace(); // 打印详细错误信息
            return false;
        }
    }

}
