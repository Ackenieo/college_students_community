package org.example.communityserver.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.communityserver.dto.FriendNotificationDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendNotificationEvent {
    private Long receiverId;
    private FriendNotificationDTO friendNotificationDTO;
}
