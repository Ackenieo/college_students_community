package org.example.communityserver.entity;

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
public class Message {
    
    @Id
    private String id;
    
    private Long senderId;
    
    private String senderUsername;
    
    private Long receiverId;
    
    private String receiverUsername;
    
    private String content;
    
    private MessageType messageType = MessageType.TEXT; // 消息类型
    
    private String attachmentUrl; // 附件URL
    
    private MessageStatus status = MessageStatus.SENT; // 消息状态
    
    private LocalDateTime createdAt;
    
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
