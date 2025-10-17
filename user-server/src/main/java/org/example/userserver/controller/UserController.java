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
     * @param request 登录请求，包含用户名/邮箱和密码
     * @return 登录响应，包含JWT token和用户信息
     */
    @PostMapping("/login")
    public Result<AuthResponseVO> login(@Valid @RequestBody LoginRequestVO request) {
        try {
            AuthResponseVO response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.INVALID_CREDENTIALS.getCode(), "登录失败: " + e.getMessage());
        }
    }
    
    // ==================== 用户注册接口 ====================
    
    /**
     * 用户注册（邮箱验证码）
     * @param request 注册请求，包含用户名、邮箱、密码等信息
     * @param code 邮箱验证码
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<UserDTO> register(
            @Valid @RequestBody RegisterRequestVO request,
            @RequestParam String code) {
        try {
            VerifyCodeRequestVO verifyRequest = new VerifyCodeRequestVO(request.getEmail(), code);
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
