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
     * @param request 登录请求，包含邮箱和密码
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

    // ====================邮箱=======================

    /**
     * 用户注册（邮箱验证码）
     * @param request 注册请求，包含用户名、邮箱、密码和验证码
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<RegisterResponseVO> register(@Valid @RequestBody RegisterRequestVO request) {
        try {
            // 构建验证码请求对象
            VerifyCodeRequestVO verifyRequest = new VerifyCodeRequestVO(request.getEmail(), request.getCode());
            
            // 微服务内部使用 DTO 进行处理
            UserDTO userDTO = userService.register(request, verifyRequest);
            
            // 返回给前端使用 VO 进行封装
            RegisterResponseVO userResponse = RegisterResponseVO.fromUserDTO(userDTO);
            return Result.success("注册成功", userResponse);
        } catch (Exception e) {
            return Result.error(ResultCode.VERIFICATION_CODE_INVALID.getCode(), "注册失败: " + e.getMessage());
        }
    }

    
    /**
     * 发送注册验证码
     * @param request 发送验证码请求，包含用户邮箱
     * @return 发送结果
     */
    @PostMapping("/send-register-code")
    public Result<String> sendRegisterVerificationCode(@Valid @RequestBody SendVerificationCodeRequestVO request) {
        try {
            userService.sendRegisterVerificationCode(request.getEmail());
            return Result.success("验证码已发送", "验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送密码重置验证码
     * @param request 发送验证码请求，包含用户邮箱
     * @return 发送结果
     */
    @PostMapping("/send-reset-code-to-email")
    public Result<String> sendPasswordResetCode(@Valid @RequestBody SendVerificationCodeRequestVO request) {
        try {
            userService.sendPasswordResetCode(request.getEmail());
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

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
     @GetMapping("/{userId}")
    public Result<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);
            return Result.success("获取用户成功", userDTO);
        } catch (Exception e) {
            return Result.error(ResultCode.USER_NOT_FOUND.getCode(), "获取用户失败: " + e.getMessage());
        }
    }


}
