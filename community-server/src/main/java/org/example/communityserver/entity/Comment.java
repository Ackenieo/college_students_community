package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
@Schema(description = "评论实体")
public class Comment {
    
    @Id
    @Schema(description = "评论ID")
    private String id;
    
    @Indexed
    @Schema(description = "帖子ID")
    private String postId;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者用户名")
    private String authorUsername;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "父评论ID，用于回复功能")
    private String parentCommentId; // 父评论ID，用于回复功能

    @Indexed
    @Schema(description = "点赞数")
    private Integer likeCount = 0;

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
}
