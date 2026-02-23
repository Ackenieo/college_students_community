package org.example.communityserver.mq.consumer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.communityserver.entity.Friendship;
import org.example.communityserver.mq.event.FriendRequestExpireEvent;
import org.example.communityserver.repository.FriendshipRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FriendRequestExpireConsumer {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @RabbitListener(queues = "dlx.queue")
    public void handleFriendRequestExpire(String message) {
        try {
            FriendRequestExpireEvent event = JSON.parseObject(message, FriendRequestExpireEvent.class);
            log.info("收到好友申请过期事件: userId={}, friendId={}, friendshipId={}",
                    event.getUserId(), event.getFriendId(), event.getFriendshipId());

            if (event.getFriendshipId() != null) {
                friendshipRepository.findById(event.getFriendshipId()).ifPresent(friendship -> {
                    if (friendship.getStatus() == Friendship.FriendshipStatus.PENDING) {
                        friendship.setStatus(Friendship.FriendshipStatus.REJECTED);
                        friendshipRepository.save(friendship);
                        log.info("好友申请已自动过期并拒绝: friendshipId={}", event.getFriendshipId());
                    }
                });
            }
        } catch (Exception e) {
            log.error("处理好友申请过期事件失败: message={}", message, e);
        }
    }
}
