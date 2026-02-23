package org.example.userserver.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 前端密码重置请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestVO {
    
    @NotBlank(message = "用户认证信息不能为空")
    @Email(message = "用户认证信息不正确")
    private String verifyInfo;

    @NotNull(message = "模式信息不能为空")
    private Short mode; // 0 为邮箱认证模式， 1 为手机号认证模式

    @NotBlank(message = "验证码不能为空")
    private String code;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    private String newPassword;
}
