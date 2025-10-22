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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "favorites")
@CompoundIndexes({
    @CompoundIndex(name = "user_post_idx", def = "{'userId': 1, 'postId': 1}", unique = true)
})
@Schema(description = "收藏实体")
public class Favorite {

    @Id
    @Schema(description = "收藏ID")
    private String id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "帖子ID")
    private String postId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
