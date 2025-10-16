package org.example.notificationserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    
    private String type; // 消息类型：NOTIFICATION, MESSAGE, SYSTEM
    
    private String title;
    
    private String content;
    
    private Object data; // 消息数据
    
    private LocalDateTime timestamp;
    
    private String senderId;
    
    private String receiverId;
    
    public static WebSocketMessage notification(String title, String content, Object data) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType("NOTIFICATION");
        message.setTitle(title);
        message.setContent(content);
        message.setData(data);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static WebSocketMessage message(String content, String senderId, String receiverId) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType("MESSAGE");
        message.setContent(content);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static WebSocketMessage system(String title, String content) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType("SYSTEM");
        message.setTitle(title);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
}
