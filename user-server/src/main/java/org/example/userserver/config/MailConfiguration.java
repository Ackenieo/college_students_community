package org.example.userserver.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件配置类
 * Spring Boot 会自动根据 application.yaml 中的 spring.mail 配置创建 JavaMailSender bean
 */
@Configuration
@ConditionalOnProperty(name = "spring.mail.host")
public class MailConfiguration {
    // Spring Boot 的邮件自动配置会处理 JavaMailSender 的创建
}
