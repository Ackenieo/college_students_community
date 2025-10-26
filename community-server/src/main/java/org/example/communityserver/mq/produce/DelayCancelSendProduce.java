package org.example.communityserver.mq.produce;

import org.example.communityserver.mq.event.DelayCancelMessageSendEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

public class DelayCancelSendProduce extends AbstractCommonSendProduceTemplate<DelayCancelMessageSendEvent> {
    public DelayCancelSendProduce(@Autowired RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(DelayCancelMessageSendEvent messageSendEvent) {
        return null;
    }

    @Override
    protected Message<?> buildMessage(DelayCancelMessageSendEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        return null;
    }
}
