package org.example.notificationserver.controller;

import org.example.notificationserver.dto.EmailRequest;
import org.example.notificationserver.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "*")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request);
            return ResponseEntity.ok("邮件发送成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("邮件发送失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<?> sendVerifyCodeEmail(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            emailService.sendVerifyCodeEmail(email, code);
            return ResponseEntity.ok("验证码邮件发送成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("验证码邮件发送失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> sendResetPasswordEmail(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            emailService.sendResetPasswordEmail(email, code);
            return ResponseEntity.ok("密码重置邮件发送成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("密码重置邮件发送失败: " + e.getMessage());
        }
    }
}
