package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@Schema(description = "消息实体")
public class Message {
    
    @Id
    @Schema(description = "消息ID")
    private String id;
    
    @Schema(description = "发送者ID")
    private Long senderId;
    
    @Schema(description = "发送者用户名")
    private String senderUsername;
    
    @Schema(description = "接收者ID")
    private Long receiverId;

    @Schema(description = "接收者用户名")
    private String receiverUsername;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型")
    private MessageType messageType = MessageType.TEXT; // 消息类型

    @Schema(description = "附件URL")
    private String attachmentUrl; // 附件URL

    @Schema(description = "消息状态")
    private MessageStatus status = MessageStatus.SENT; // 消息状态

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "阅读时间")
    private LocalDateTime readAt; // 阅读时间
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum MessageType {
        TEXT, IMAGE, FILE
    }
    
    public enum MessageStatus {
        SENT, DELIVERED, READ, FAILED
    }
}
