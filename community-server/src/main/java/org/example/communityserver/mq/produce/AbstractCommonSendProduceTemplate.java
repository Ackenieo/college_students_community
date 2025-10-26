package org.example.communityserver.mq.produce;

import jakarta.websocket.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCommonSendProduceTemplate<T> {
     private final RabbitTemplate rabbitTemplate;

     /**
      * 构建消息发送事件基础扩充属性实体
      *
      * @param messageSendEvent 消息发送事件
      * @return 扩充属性实体
      */
     protected abstract BaseSendExtendDTO buildBaseSendExtendParam(T messageSendEvent);

     /**
      * 构建消息发送事件消息体
      *
      * @param messageSendEvent 消息发送事件
      * @param requestParam     扩充属性实体
      * @return 消息体
      */
     protected abstract Message<?> buildMessage(T messageSendEvent, BaseSendExtendDTO requestParam);

     /**
      * 消息事件通用发送
      *
      * @param messageSendEvent 消息发送事件
      * @return 消息发送返回结果
      */
     protected boolean sendMessage(T messageSendEvent) {
          BaseSendExtendDTO baseSendExtendParam = buildBaseSendExtendParam(messageSendEvent);
          boolean sendResult;
          try {
               Message<?> message = buildMessage(messageSendEvent, baseSendExtendParam);
               // TODO: 实现异步消息的确认回调
               rabbitTemplate.convertAndSend(baseSendExtendParam.getExchange(), baseSendExtendParam.getRoutingKey(), message);
               sendResult = true;
          } catch (Exception e) {
               log.error("消息发送失败, 事件名称: {}, 交换机: {}, 路由键: {}", baseSendExtendParam.getEventName(), baseSendExtendParam.getExchange(), baseSendExtendParam.getRoutingKey(), e);
               throw new RuntimeException("消息发送失败", e);
          }
          return sendResult;
     }


}
