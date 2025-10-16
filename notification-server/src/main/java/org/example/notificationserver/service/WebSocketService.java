package org.example.notificationserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notificationserver.dto.WebSocketMessage;
import org.example.notificationserver.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 在线用户会话管理
    private final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();
    
    public void sendNotificationToUser(String userId, Notification notification) {
        try {
            WebSocketMessage message = WebSocketMessage.notification(
                notification.getTitle(),
                notification.getContent(),
                notification
            );
            
            String destination = "/topic/notifications/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            
            // 同时通过Redis发布，用于跨服务器实例通信
            String redisChannel = "notifications:" + userId;
            String messageJson = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(redisChannel, messageJson);
            
            logger.info("发送通知到用户: {}, 通知ID: {}", userId, notification.getId());
        } catch (Exception e) {
            logger.error("发送通知失败: userId={}, notificationId={}", userId, notification.getId(), e);
        }
    }
    
    public void sendMessageToUser(String userId, String content, String senderId) {
        try {
            WebSocketMessage message = WebSocketMessage.message(content, senderId, userId);
            
            String destination = "/topic/messages/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            
            logger.info("发送消息到用户: {}, 发送者: {}", userId, senderId);
        } catch (Exception e) {
            logger.error("发送消息失败: userId={}, senderId={}", userId, senderId, e);
        }
    }
    
    public void sendSystemMessage(String title, String content) {
        try {
            WebSocketMessage message = WebSocketMessage.system(title, content);
            
            // 广播给所有在线用户
            messagingTemplate.convertAndSend("/topic/system", message);
            
            logger.info("发送系统消息: {}", title);
        } catch (Exception e) {
            logger.error("发送系统消息失败: {}", title, e);
        }
    }
    
    public void broadcastToAllUsers(String message) {
        try {
            messagingTemplate.convertAndSend("/topic/broadcast", message);
            logger.info("广播消息给所有用户");
        } catch (Exception e) {
            logger.error("广播消息失败", e);
        }
    }
    
    public void addUserSession(String userId, String sessionId) {
        userSessions.put(userId, sessionId);
        logger.info("用户上线: userId={}, sessionId={}", userId, sessionId);
    }
    
    public void removeUserSession(String userId) {
        userSessions.remove(userId);
        logger.info("用户下线: userId={}", userId);
    }
    
    public boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId);
    }
    
    public int getOnlineUserCount() {
        return userSessions.size();
    }
}
