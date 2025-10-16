package org.example.communityserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequest {
    
    @NotNull(message = "好友ID不能为空")
    private Long friendId;
    
    @NotBlank(message = "好友邮箱或用户名不能为空")
    private String searchKeyword; // 可以是邮箱或用户名
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String username;
}
