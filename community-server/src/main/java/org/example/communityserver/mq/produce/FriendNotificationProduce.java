package org.example.communityserver.mq.produce;

import lombok.extern.slf4j.Slf4j;
import org.example.communityserver.mq.event.FriendNotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.example.common.constant.CommonConstants.MessageQueue.*;

@Slf4j
@Component
public class FriendNotificationProduce extends AbstractCommonSendProduceTemplate<FriendNotificationEvent> {

    @Autowired
    public FriendNotificationProduce(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(FriendNotificationEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("好友通知事件")
                .exchange(DELAY_EXCHANGE)
                .routingKey(DELAY_ROUTING_KEY)
                .build();
    }

    @Override
    protected Message<?> buildMessage(FriendNotificationEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        return MessageBuilder.withPayload(messageSendEvent)
                .setHeader("messageId", UUID.randomUUID().toString())
                .setHeader("timestamp", System.currentTimeMillis())
                .setHeader("eventType", "FRIEND_NOTIFICATION")
                .build();
    }

    public void sendFriendNotification(FriendNotificationEvent event) {
        sendMessage(event);
        log.info("好友通知已发送: receiverId={}", event.getReceiverId());
    }
}
