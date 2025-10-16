package org.example.userserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime expiresAt;
    
    public static AuthResponse success(String token, Long userId, String username, 
                                     String email, String role, LocalDateTime expiresAt) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);
        response.setRole(role);
        response.setExpiresAt(expiresAt);
        return response;
    }
}
