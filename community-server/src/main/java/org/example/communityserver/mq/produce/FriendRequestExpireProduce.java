package org.example.communityserver.mq.produce;

import org.example.communityserver.mq.event.FriendRequestExpireEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.example.common.constant.CommonConstants.MessageQueue.*;

@Component
public class FriendRequestExpireProduce extends AbstractCommonSendProduceTemplate<FriendRequestExpireEvent> {

    public FriendRequestExpireProduce(@Autowired RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(FriendRequestExpireEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("好友申请过期事件")
                .exchange(DELAY_EXCHANGE)
                .routingKey(DELAY_ROUTING_KEY)
                .build();
    }

    @Override
    protected Message<?> buildMessage(FriendRequestExpireEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        return MessageBuilder.withPayload(messageSendEvent)
                .setHeader("messageId", UUID.randomUUID().toString())
                .setHeader("timestamp", System.currentTimeMillis())
                .setHeader("eventType", "FRIEND_REQUEST_EXPIRE")
                .build();
    }

    public void sendFriendRequestExpireMessage(Long userId, Long friendId, String friendshipId, long delayMillis) {
        FriendRequestExpireEvent event = FriendRequestExpireEvent.builder()
                .userId(userId)
                .friendId(friendId)
                .friendshipId(friendshipId)
                .build();

        sendMessageWithDelay(event, delayMillis);
    }
}
