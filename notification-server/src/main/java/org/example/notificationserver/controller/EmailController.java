package org.example.notificationserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.notificationserver.dto.EmailRequest;
import org.example.notificationserver.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "*")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send")
    public Result<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request);
            return Result.success("邮件发送成功", "邮件已发送");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "邮件发送失败: " + e.getMessage());
        }
    }
    
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
