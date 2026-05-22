package org.example.userserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.userserver.entity.User;

import java.time.format.DateTimeFormatter;

/**
 * 服务内用户数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String fullName;
    private String phone;
    private User.UserRole role;
    private User.UserStatus status;
    
    private String createdAt;
    private String lastLogin;
    
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        
        // 转换 LocalDateTime 为 String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null);
        dto.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().format(formatter) : null);
        
        return dto;
    }
}
