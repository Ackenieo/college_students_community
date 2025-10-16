package org.example.communityserver.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    // TODO: 实现通知服务
    // 这里暂时为空，后续会集成实际的通知功能
    
    public void sendFriendRequestNotification(Long receiverId, Long senderId) {
        // TODO: 发送好友申请通知
        System.out.println("发送好友申请通知: " + senderId + " -> " + receiverId);
    }
    
    public void sendMessageNotification(Long receiverId, Long senderId) {
        // TODO: 发送消息通知
        System.out.println("发送消息通知: " + senderId + " -> " + receiverId);
    }
}
