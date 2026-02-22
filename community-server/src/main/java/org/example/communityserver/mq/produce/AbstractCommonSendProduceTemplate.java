package org.example.communityserver.mq.produce;

import jakarta.websocket.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
      * 消息事件通用发送（同步方式，等待确认结果）
      *
      * @param messageSendEvent 消息发送事件
      * @return 消息发送返回结果
      */
     protected boolean sendMessage(T messageSendEvent) {
          // 调用子类实现的抽象方法，构建消息发送的基础属性（交换机、路由键等）
          BaseSendExtendDTO baseSendExtendParam = buildBaseSendExtendParam(messageSendEvent);
          // 初始化发送结果变量
          boolean sendResult;
          try {
               // 调用子类实现的抽象方法，构建要发送的消息对象
               Message<?> message = buildMessage(messageSendEvent, baseSendExtendParam);
               
               // 使用CompletableFuture来异步等待RabbitMQ的确认回调
               CompletableFuture<Boolean> confirmFuture = new CompletableFuture<>();
               
               // TODO: 实现异步消息的确认回调 - 已完善: 使用CompletableFuture实现异步回调
               // 设置RabbitTemplate的确认回调，用于异步接收消息发送的确认结果
               rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                    // 如果消息发送失败（ack为false），记录错误日志并完成Future
                    if (!ack) {
                         log.error("消息发送失败, 事件名称: {}, 错误原因: {}", baseSendExtendParam.getEventName(), cause);
                         confirmFuture.complete(false);
                    } else {
                         // 如果消息发送成功（ack为true），记录成功日志并完成Future
                         log.info("消息发送成功, 事件名称: {}", baseSendExtendParam.getEventName());
                         confirmFuture.complete(true);
                    }
               });
               
               // 使用RabbitTemplate发送消息到指定的交换机和路由键
               rabbitTemplate.convertAndSend(baseSendExtendParam.getExchange(), baseSendExtendParam.getRoutingKey(), message);
               
               // 等待确认回调完成，最多等待5秒
               try {
                    sendResult = confirmFuture.get(5, TimeUnit.SECONDS);
                    log.info("消息发送确认完成, 事件名称: {}, 结果: {}", baseSendExtendParam.getEventName(), sendResult);
               } catch (TimeoutException e) {
                    log.warn("等待消息确认超时, 事件名称: {}", baseSendExtendParam.getEventName());
                    sendResult = false;
               } catch (Exception e) {
                    log.error("等待消息确认异常, 事件名称: {}", baseSendExtendParam.getEventName(), e);
                    sendResult = false;
               }
               
          } catch (Exception e) {
               // 捕获发送过程中的异常，记录详细的错误日志（包含事件名称、交换机、路由键）
               log.error("消息发送失败, 事件名称: {}, 交换机: {}, 路由键: {}", baseSendExtendParam.getEventName(), baseSendExtendParam.getExchange(), baseSendExtendParam.getRoutingKey(), e);
               // 抛出运行时异常，告知上层调用者消息发送失败
               throw new RuntimeException("消息发送失败", e);
          }
          // 返回消息发送结果
          return sendResult;
     }

     protected void sendMessageWithDelay(T messageSendEvent, long delayMillis) {
          BaseSendExtendDTO baseSendExtendParam = buildBaseSendExtendParam(messageSendEvent);
          try {
               Message<?> message = buildMessage(messageSendEvent, baseSendExtendParam);
               rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                    if (!ack) {
                         log.error("消息发送失败, 事件名称: {}, 错误原因: {}", baseSendExtendParam.getEventName(), cause);
                    } else {
                         log.info("消息发送成功, 事件名称: {}", baseSendExtendParam.getEventName());
                    }
               });
               rabbitTemplate.convertAndSend(
                       baseSendExtendParam.getExchange(),
                       baseSendExtendParam.getRoutingKey(),
                       message,
                       msg -> {
                           msg.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                           return msg;
                       }
               );
          } catch (Exception e) {
               log.error("消息发送失败, 事件名称: {}, 交换机: {}, 路由键: {}", baseSendExtendParam.getEventName(), baseSendExtendParam.getExchange(), baseSendExtendParam.getRoutingKey(), e);
               throw new RuntimeException("消息发送失败", e);
          }
     }

}
