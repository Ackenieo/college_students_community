package org.example.userserver.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime expiresAt;
    
    public static AuthResponseVO success(String token, Long userId, String username, 
                                       String email, String role, LocalDateTime expiresAt) {
        AuthResponseVO response = new AuthResponseVO();
        response.setToken(token);
        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);
        response.setRole(role);
        response.setExpiresAt(expiresAt);
        return response;
    }
}
