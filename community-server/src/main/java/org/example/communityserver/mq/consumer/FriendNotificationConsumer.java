package org.example.communityserver.mq.consumer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.communityserver.mq.event.FriendNotificationEvent;
import org.example.communityserver.service.NotificationService;
import org.example.communityserver.webSocket.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FriendNotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = "dlx.queue")
    public void handleFriendNotification(String message) {
        try {
            FriendNotificationEvent event = JSON.parseObject(message, FriendNotificationEvent.class);
            log.info("收到好友通知事件: receiverId={}", event.getReceiverId());

            if (event.getFriendNotificationDTO() != null) {
                notificationService.sendFriendRequestNotification(event.getReceiverId(), event.getFriendNotificationDTO());
            }
        } catch (Exception e) {
            log.error("处理好友通知事件失败: message={}", message, e);
        }
    }
}
