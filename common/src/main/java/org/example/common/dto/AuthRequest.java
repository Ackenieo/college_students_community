package org.example.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 认证请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String password;
    }
    
    /**
     * 注册请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
        private String username;
        
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String password;
        
        @Size(min = 6, max = 6, message = "验证码必须是6位数字")
        private String verificationCode;
        
        private String nickname;
    }
    
    /**
     * 验证码请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyCodeRequest {
        
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        /**
         * 验证码类型：register-注册，reset-重置密码
         */
        @NotBlank(message = "验证码类型不能为空")
        private String type;
    }
    
    /**
     * 密码重置请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordResetRequest {
        
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        @Size(min = 6, max = 6, message = "验证码必须是6位数字")
        private String verificationCode;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String newPassword;
    }
}
