package org.example.communityserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
@CompoundIndexes({
    @CompoundIndex(name = "user_target_idx", def = "{'userId': 1, 'targetId': 1, 'targetType': 1}", unique = true)
})
public class Like {
    
    @Id
    private String id;
    
    private Long userId;
    
    @Indexed
    private String targetId; // 目标ID（帖子ID或评论ID）
    
    private LikeType targetType; // 点赞类型：POST或COMMENT
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum LikeType {
        POST, COMMENT
    }
}
