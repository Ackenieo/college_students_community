package org.example.communityserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    
    @Id
    private String id;
    
    @Indexed
    private Long authorId;
    
    private String authorUsername;
    
    private String authorEmail;
    
    private String title;
    
    private String content;
    
    private List<String> images; // 图片URL列表
    
    private List<String> tags; // 标签列表
    
    @Indexed
    private PostStatus status = PostStatus.PENDING; // 审核状态
    
    private String reviewResult; // 审核结果
    
    private String reviewReason; // 审核原因
    
    private Integer likeCount = 0;
    
    private Integer commentCount = 0;
    
    private Integer shareCount = 0;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PostStatus {
        PENDING,    // 待审核
        APPROVED,   // 已通过
        REJECTED,   // 已拒绝
        DELETED     // 已删除
    }
}
