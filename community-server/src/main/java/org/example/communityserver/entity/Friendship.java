package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friendships")
@CompoundIndexes({
    @CompoundIndex(name = "user_friend_idx", def = "{'userId': 1, 'friendId': 1}", unique = true)
})
@Schema(description = "好友关系实体")
public class Friendship {

    @Id
    @Schema(description = "好友关系ID")
    private String id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "好友ID")
    private Long friendId;

    @Schema(description = "好友关系状态")
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum FriendshipStatus {
        PENDING,    // 待确认
        ACCEPTED,   // 已接受
        REJECTED,   // 已拒绝
        BLOCKED     // 已拉黑
    }
}
