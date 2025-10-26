package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "好友通知数据传输对象")
public class FriendNotificationDTO {

    @Schema(description = "通知ID")
    private Long notificationStatus; // 暂时不知道有什么用

    @Schema(description = "申请人ID")
    private Long senderId; // 申请人ID（userId）

    @Schema(description = "申请人昵称")
    private String senderNickname; // 申请人昵称（如“张三”）

    @Schema(description = "申请人头像URL")
    private String senderAvatar; // 申请人头像URL（客户端展示头像用）

    @Schema(description = "好友申请备注信息")
    private String requestMessage; // 申请备注（如“我是XX班的李四”）

    @Schema(description = "通知类型")
    private String notificationType; // 通知类型（固定为“FRIEND_REQUEST”，客户端区分业务）

    @Schema(description = "通知创建时间")
    private LocalDateTime createTime; // 通知创建时间
}
