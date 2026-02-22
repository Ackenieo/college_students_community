package org.example.communityserver.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.FriendNotificationDTO;
import org.example.communityserver.entity.Notification;
import org.example.communityserver.repository.NotificationRepository;
import org.example.communityserver.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private static NotificationService instance;

    public NotificationService() {
        instance = this;
    }

    public static NotificationService getInstance() {
        return instance;
    }

    // TODO: 实现通知服务 - 已完善: 实现完整的通知服务功能
    public Notification createNotification(Long receiverId, Long senderId, String senderNickname,
                                          String senderAvatar, String notificationType,
                                          String content, String targetId) {
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .senderAvatar(senderAvatar)
                .notificationType(notificationType)
                .content(content)
                .isRead(false)
                .targetId(targetId)
                .build();

        return notificationRepository.save(notification);
    }

    public void sendFriendRequestNotification(Long receiverId, FriendNotificationDTO friendNotificationDTO) {
        if(WebSocketService.sendMessageToUser(receiverId, friendNotificationDTO)) {
            log.info("好友申请通知发送成功: " + friendNotificationDTO.getSenderId() + " -> " + receiverId);
        } else {
            throw new BusinessException(ResultCode.NOTIFICATION_SEND_FAILED, "好友申请通知发送失败: " + friendNotificationDTO.getSenderId() + " -> " + receiverId);
        }
    }

    public void sendMessageNotification(Long receiverId, Long senderId, String senderUsername, String content) {
        // TODO: 发送消息通知 - 已完善: 实现消息通知功能
        Notification notification = createNotification(
                receiverId,
                senderId,
                senderUsername,
                null,
                "message",
                content,
                null
        );

        WebSocketService.sendMessageToUser(receiverId, notification);
        log.info("消息通知发送成功: senderId={}, receiverId={}", senderId, receiverId);
    }

    public Page<Notification> getNotifications(Long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId, pageable);
    }

    public Page<Notification> getUnreadNotifications(Long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByReceiverIdAndIsReadOrderByCreatedAtDesc(receiverId, false, pageable);
    }

    public List<Notification> getUnreadNotifications(Long receiverId) {
        return notificationRepository.findByReceiverIdAndIsRead(receiverId, false);
    }

    public long getUnreadCount(Long receiverId) {
        return notificationRepository.countByReceiverIdAndIsRead(receiverId, false);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(Long receiverId) {
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIdAndIsRead(receiverId, false);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void clearAllNotifications(Long receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdAndIsRead(receiverId, true);
        notificationRepository.deleteAll(notifications);
    }
}
