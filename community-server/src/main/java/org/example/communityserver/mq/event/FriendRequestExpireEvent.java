package org.example.communityserver.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestExpireEvent {
    private String friendshipId;
    private Long userId;
    private Long friendId;
}
