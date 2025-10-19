package org.example.userserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.userserver.dto.*;
import org.example.userserver.vo.*;
import org.example.userserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 提供用户注册、登录、信息管理等功能
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    
    // ==================== 用户认证接口 ====================
    
    /**
     * 用户登录
     * @param email 用户邮箱
     * @param password 用户密码
     * @return 登录响应，包含JWT token和用户信息
     */
    @PostMapping("/login")
    public Result<AuthResponseVO> login(
            @RequestParam String email,
            @RequestParam String password) {
        try {
            // 构建登录请求对象
            LoginRequestVO request = new LoginRequestVO();
            request.setEmail(email);
            request.setPassword(password);
            
            AuthResponseVO response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.INVALID_CREDENTIALS.getCode(), "登录失败: " + e.getMessage());
        }
    }

    
    // ==================== 用户注册接口 ====================
    
    /**
     * 用户注册（邮箱验证码）
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param code 邮箱验证码
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<UserDTO> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String code) {
        try {
            // 构建注册请求对象
            RegisterRequestVO request = new RegisterRequestVO();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            
            // 构建验证码请求对象
            VerifyCodeRequestVO verifyRequest = new VerifyCodeRequestVO(email, code);
            
            UserDTO user = userService.register(request, verifyRequest);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(ResultCode.VERIFICATION_CODE_INVALID.getCode(), "注册失败: " + e.getMessage());
        }
    }

    
    /**
     * 发送注册验证码
     * @param email 用户邮箱
     * @return 发送结果
     */
    @PostMapping("/send-register-code")
    public Result<String> sendRegisterVerificationCode(@RequestParam String email) {
        try {
            userService.sendRegisterVerificationCode(email);
            return Result.success("验证码已发送", "验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送密码重置验证码
     * @param email 用户邮箱
     * @return 发送结果
     */
    @PostMapping("/send-reset-code")
    public Result<String> sendPasswordResetCode(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);
            return Result.success("验证码已发送", "验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置密码
     * @param request 密码重置请求，包含邮箱、验证码和新密码
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody PasswordResetRequestVO request) {
        try {
            userService.resetPassword(request);
            return Result.success("密码重置成功", "密码重置成功，请使用新密码登录");
        } catch (Exception e) {
            return Result.error(ResultCode.VERIFICATION_CODE_INVALID.getCode(), "重置失败: " + e.getMessage());
        }
    }
    

}
