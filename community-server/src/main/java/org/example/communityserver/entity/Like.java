package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "点赞实体")
public class Like {

    @Id
    @Schema(description = "点赞ID")
    private String id;

    @Schema(description = "用户ID")
    private Long userId;

    @Indexed
    @Schema(description = "目标ID（帖子ID或评论ID）")
    private String targetId; // 目标ID（帖子ID或评论ID）

    @Schema(description = "点赞类型：POST或COMMENT")
    private LikeType targetType; // 点赞类型：POST或COMMENT

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum LikeType {
        POST, COMMENT
    }
}
