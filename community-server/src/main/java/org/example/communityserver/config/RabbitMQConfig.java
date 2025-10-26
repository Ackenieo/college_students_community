package org.example.communityserver.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.example.common.constant.CommonConstants.MessageQueue.*;

@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
    }

    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(DELAY_EXCHANGE);
    }
    @Bean
    public Queue normalQueue() {
        Map<String, Object> args = new HashMap<>();
        // 关键：消息过期后转发到死信交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        // 关键：死信的路由键（用于绑定死信队列）
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        return QueueBuilder.durable(DELAY_QUEUE).withArguments(args).build();
    }
    @Bean
    public Binding normalBinding() {
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(DELAY_ROUTING_KEY);
    }

}
