package org.example.userserver.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.userserver.dto.UserDTO;

/**
 * 用户注册响应VO
 * 用于返回给前端的用户注册结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseVO {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String createdAt;
    private String lastLogin;
    
    /**
     * 从 UserDTO 转换为 RegisterResponseVO
     */
    public static RegisterResponseVO fromUserDTO(UserDTO userDTO) {
        RegisterResponseVO vo = new RegisterResponseVO();
        vo.setId(userDTO.getId());
        vo.setUsername(userDTO.getUsername());
        vo.setEmail(userDTO.getEmail());
        vo.setFullName(userDTO.getFullName());
        vo.setPhone(userDTO.getPhone());
        vo.setRole(userDTO.getRole() != null ? userDTO.getRole().name() : null);
        vo.setStatus(userDTO.getStatus() != null ? userDTO.getStatus().name() : null);
        vo.setCreatedAt(userDTO.getCreatedAt());
        vo.setLastLogin(userDTO.getLastLogin());
        return vo;
    }
}
