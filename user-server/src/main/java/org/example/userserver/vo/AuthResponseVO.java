package org.example.userserver.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 前端登录响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseVO {
    
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
    
    private String expiresAt;
    
    public static AuthResponseVO success(String token, Long userId, String username, 
                                       String email, String role, LocalDateTime expiresAt) {
        AuthResponseVO response = new AuthResponseVO();
        response.setToken(token);
        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);
        response.setRole(role);
        // 转换 LocalDateTime 为 String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.setExpiresAt(expiresAt != null ? expiresAt.format(formatter) : null);
        return response;
    }
}
