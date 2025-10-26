package org.example.communityserver.mq.produce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseSendExtendDTO {
     private String eventName;
     private String exchange;
     private String routingKey;
     private Long sentTimeOut;
     private Long delayTime;

}
