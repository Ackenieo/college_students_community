package org.example.communityserver.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.FriendNotificationDTO;
import org.example.communityserver.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationService {

    // TODO: 实现通知服务
    // 这里暂时为空，后续会集成实际的通知功能
    
//    public void sendFriendRequestNotification(Long receiverId, Long senderId) {
//
//
//        System.out.println("发送好友申请通知: " + senderId + " -> " + receiverId);
//
//    }

    public void sendFriendRequestNotification(Long receiverId, FriendNotificationDTO friendNotificationDTO) {
        if(WebSocketService.sendMessageToUser(receiverId, friendNotificationDTO)) {
            log.info("好友申请通知发送成功: " + friendNotificationDTO.getSenderId() + " -> " + receiverId);
        } else {
            throw new BusinessException(ResultCode.NOTIFICATION_SEND_FAILED, "好友申请通知发送失败: " + friendNotificationDTO.getSenderId() + " -> " + receiverId);
        }
    }
    
    public void sendMessageNotification(Long receiverId, Long senderId) {
        // TODO: 发送消息通知
        System.out.println("发送消息通知: " + senderId + " -> " + receiverId);
    }
}
