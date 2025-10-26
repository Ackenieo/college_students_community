package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "好友请求")
public class FriendshipRequest {

    @Schema(description = "请求发送者ID")
    @NotNull(message = "请求发送者ID不能为空")
    private Long senderId;

    @Schema(description = "好友ID")
    @NotNull(message = "好友ID不能为空")
    private Long friendId;

//    @Schema(description = "好友邮箱或用户名")
//    @NotBlank(message = "好友邮箱或用户名不能为空")
//    private String searchKeyword; // 可以是邮箱或用户名

//    @Schema(description = "好友邮箱")
//    @Email(message = "邮箱格式不正确")
//    private String email;

    @Schema(description = "请求发送者用户名")
    private String senderName;

    @Schema(description = "好友申请备注信息")
    private String requestMessage;
}
