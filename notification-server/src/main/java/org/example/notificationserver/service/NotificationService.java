package org.example.notificationserver.service;

import org.example.notificationserver.dto.NotificationRequest;
import org.example.notificationserver.entity.Notification;
import org.example.notificationserver.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private WebSocketService webSocketService;
    
    public Notification createNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setReceiverId(request.getReceiverId());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setType(Notification.NotificationType.valueOf(request.getType()));
        notification.setPriority(Notification.NotificationPriority.valueOf(request.getPriority()));
        notification.setCategory(request.getCategory());
        notification.setData(request.getData());
        notification.setActionUrl(request.getActionUrl());
        notification.setStatus(Notification.NotificationStatus.UNREAD);
        
        if (request.getExpireTime() != null) {
            notification.setExpireAt(LocalDateTime.ofEpochSecond(request.getExpireTime() / 1000, 0, java.time.ZoneOffset.UTC));
        }
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // 通过WebSocket发送实时通知
        webSocketService.sendNotificationToUser(request.getReceiverId().toString(), savedNotification);
        
        return savedNotification;
    }
    
    public Page<Notification> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, Notification.NotificationStatus.UNREAD);
    }
    
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
    }
    
    public Notification markAsRead(String notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findByIdAndReceiverId(notificationId, userId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(Notification.NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        return null;
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, Notification.NotificationStatus.UNREAD);
        for (Notification notification : unreadNotifications) {
            notification.setStatus(Notification.NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }
    
    public void deleteNotification(String notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findByIdAndReceiverId(notificationId, userId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(Notification.NotificationStatus.DELETED);
            notificationRepository.save(notification);
        }
    }
    
    public void deleteExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findByExpireAtBefore(LocalDateTime.now());
        for (Notification notification : expiredNotifications) {
            notification.setStatus(Notification.NotificationStatus.DELETED);
            notificationRepository.save(notification);
        }
    }
    
    // 便捷方法：发送好友申请通知
    public void sendFriendRequestNotification(Long receiverId, Long senderId, String senderUsername) {
        NotificationRequest request = new NotificationRequest();
        request.setReceiverId(receiverId);
        request.setTitle("新的好友申请");
        request.setContent(senderUsername + " 想要添加您为好友");
        request.setType("FRIEND_REQUEST");
        request.setPriority("NORMAL");
        request.setCategory("friend");
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("senderId", senderId);
        data.put("senderUsername", senderUsername);
        request.setData(data);
        
        createNotification(request);
    }
    
    // 便捷方法：发送消息通知
    public void sendMessageNotification(Long receiverId, Long senderId, String senderUsername, String messagePreview) {
        NotificationRequest request = new NotificationRequest();
        request.setReceiverId(receiverId);
        request.setTitle("新消息");
        request.setContent(senderUsername + ": " + messagePreview);
        request.setType("MESSAGE");
        request.setPriority("HIGH");
        request.setCategory("message");
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("senderId", senderId);
        data.put("senderUsername", senderUsername);
        data.put("messagePreview", messagePreview);
        request.setData(data);
        
        createNotification(request);
    }
    
    // 便捷方法：发送点赞通知
    public void sendLikeNotification(Long receiverId, Long senderId, String senderUsername, String targetType, String targetId) {
        NotificationRequest request = new NotificationRequest();
        request.setReceiverId(receiverId);
        request.setTitle("收到新的点赞");
        request.setContent(senderUsername + " 点赞了您的" + ("POST".equals(targetType) ? "帖子" : "评论"));
        request.setType("LIKE");
        request.setPriority("NORMAL");
        request.setCategory("like");
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("senderId", senderId);
        data.put("senderUsername", senderUsername);
        data.put("targetType", targetType);
        data.put("targetId", targetId);
        request.setData(data);
        
        createNotification(request);
    }
    
    // 便捷方法：发送评论通知
    public void sendCommentNotification(Long receiverId, Long senderId, String senderUsername, String commentPreview) {
        NotificationRequest request = new NotificationRequest();
        request.setReceiverId(receiverId);
        request.setTitle("收到新的评论");
        request.setContent(senderUsername + " 评论了您的帖子: " + commentPreview);
        request.setType("COMMENT");
        request.setPriority("NORMAL");
        request.setCategory("comment");
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("senderId", senderId);
        data.put("senderUsername", senderUsername);
        data.put("commentPreview", commentPreview);
        request.setData(data);
        
        createNotification(request);
    }
}
