package org.example.userserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.userserver.dto.*;
import org.example.userserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO user = userService.createUser(request);
            return Result.success("用户创建成功", user);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "用户创建失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> Result.success("查询成功", user))
                .orElse(Result.error(ResultCode.USER_NOT_FOUND));
    }
    
    @GetMapping("/username/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> Result.success("查询成功", user))
                .orElse(Result.error(ResultCode.USER_NOT_FOUND));
    }
    
    @GetMapping
    public Result<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return Result.success("查询成功", users);
    }
    
    @GetMapping("/role/{role}")
    public Result<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            List<UserDTO> users = userService.getUsersByRole(role);
            return Result.success("查询成功", users);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "查询失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public Result<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return Result.success("搜索成功", users);
    }
    
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, 
                                            @Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO user = userService.updateUser(id, request);
            return Result.success("用户更新成功", user);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "用户更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("用户删除成功", null);
        } catch (Exception e) {
            return Result.error(ResultCode.USER_NOT_FOUND.getCode(), "用户删除失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/login")
    public Result<Void> updateLastLogin(@PathVariable Long id) {
        try {
            userService.updateLastLogin(id);
            return Result.success("登录时间更新成功", null);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "更新失败: " + e.getMessage());
        }
    }
    
    // 认证相关接口
    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.INVALID_CREDENTIALS.getCode(), "登录失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDTO user = userService.register(request);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(ResultCode.USER_ALREADY_EXISTS.getCode(), "注册失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/register-with-verification")
    public Result<UserDTO> registerWithVerification(
            @Valid @RequestBody RegisterRequest request,
            @RequestParam String code) {
        try {
            VerifyCodeRequest verifyRequest = new VerifyCodeRequest(request.getEmail(), code);
            UserDTO user = userService.registerWithVerification(request, verifyRequest);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(ResultCode.VERIFICATION_CODE_INVALID.getCode(), "注册失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/send-register-code")
    public Result<String> sendRegisterVerificationCode(@RequestParam String email) {
        try {
            userService.sendRegisterVerificationCode(email);
            return Result.success("验证码已发送", "验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "发送失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/send-reset-code")
    public Result<String> sendPasswordResetCode(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);
            return Result.success("验证码已发送", "验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.error(ResultCode.EMAIL_SEND_FAILED.getCode(), "发送失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request);
            return Result.success("密码重置成功", "密码重置成功，请使用新密码登录");
        } catch (Exception e) {
            return Result.error(ResultCode.VERIFICATION_CODE_INVALID.getCode(), "重置失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/email/{email}")
    public Result<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> Result.success("查询成功", user))
                .orElse(Result.error(ResultCode.USER_NOT_FOUND));
    }
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常", "User service is running");
    }
}
