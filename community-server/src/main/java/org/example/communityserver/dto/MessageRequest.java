package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息请求DTO")
public class MessageRequest {

    @Schema(description = "接收者ID")
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @Schema(description = "发送者ID")
    @NotNull(message = "发送者ID不能为空")
    private Long senderId;

    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息长度不能超过2000个字符")
    private String content;

    @Schema(description = "消息类型")
    private String messageType = "TEXT"; // 消息类型：TEXT, IMAGE, FILE

    @Schema(description = "附件URL")
    private String attachmentUrl; // 附件URL
}
