package org.example.notificationserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private Long receiverId;
    
    private String title;
    
    private String content;
    
    private NotificationType type = NotificationType.GENERAL;
    
    private NotificationPriority priority = NotificationPriority.NORMAL;
    
    private String category;
    
    private Map<String, Object> data;
    
    private String actionUrl;
    
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime readAt;
    
    private LocalDateTime expireAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        GENERAL,        // 一般通知
        FRIEND_REQUEST, // 好友申请
        MESSAGE,        // 消息通知
        LIKE,          // 点赞通知
        COMMENT,       // 评论通知
        SYSTEM         // 系统通知
    }
    
    public enum NotificationPriority {
        HIGH, NORMAL, LOW
    }
    
    public enum NotificationStatus {
        UNREAD, READ, DELETED
    }
}
