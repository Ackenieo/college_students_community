package org.example.userserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 服务内更新用户请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(min = 6, message = "密码长度至少6个字符")
    private String password;
    
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String fullName;
    
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;
    
    private String role;
}
