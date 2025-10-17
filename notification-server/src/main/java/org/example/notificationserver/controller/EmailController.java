package org.example.notificationserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.notificationserver.dto.EmailRequest;
import org.example.notificationserver.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 邮件通知控制器
 * 提供邮件发送服务，包括普通邮件、验证码邮件、密码重置邮件等
 */
@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "*")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 发送普通邮件
     * @param request 邮件发送请求，包含收件人、主题、内容等信息
     * @return 发送结果
     */
    @PostMapping("/send")
    public Result<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request);
            return Result.success("邮件发送成功", "邮件已发送");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送验证码邮件
     * @param email 收件人邮箱
     * @param code 验证码
     * @return 发送结果
     */
    @PostMapping("/verify-code")
    public Result<String> sendVerifyCodeEmail(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            emailService.sendVerifyCodeEmail(email, code);
            return Result.success("验证码邮件发送成功", "验证码邮件已发送");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "验证码邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送密码重置邮件
     * @param email 收件人邮箱
     * @param code 重置验证码
     * @return 发送结果
     */
    @PostMapping("/reset-password")
    public Result<String> sendResetPasswordEmail(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            emailService.sendResetPasswordEmail(email, code);
            return Result.success("密码重置邮件发送成功", "密码重置邮件已发送");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "密码重置邮件发送失败: " + e.getMessage());
        }
    }
}
