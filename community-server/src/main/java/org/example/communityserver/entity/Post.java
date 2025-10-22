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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
@Schema(description = "帖子实体")
public class Post {
    
    @Id
    @Schema(description = "帖子ID")
    private String id; // 帖子ID
    
    @Indexed
    @Schema(description = "作者ID")
    private Long authorId; // 作者ID

    @Schema(description = "作者用户名")
    private String authorUsername; // 作者用户名
    
    @Schema(description = "作者邮箱")
    private String authorEmail; // 作者邮箱
    
    @Schema(description = "帖子标题")
    private String title; // 帖子标题

    @Schema(description = "帖子内容")
    private String content; // 帖子内容

    @Schema(description = "图片URL列表")
    private List<String> images; // 图片URL列表

    @Schema(description = "标签列表")
    private List<String> tags; // 标签列表
    
    @Indexed
    @Schema(description = "审核状态")
    private PostStatus status = PostStatus.PENDING; // 审核状态

    @Schema(description = "审核结果")
    private String reviewResult; // 审核结果

    @Schema(description = "审核原因")
    private String reviewReason; // 审核原因

    @Schema(description = "点赞数")
    private Integer likeCount = 0; // 点赞数

    @Schema(description = "评论数")
    private Integer commentCount = 0; // 评论数

    @Schema(description = "分享数")
    private Integer shareCount = 0; // 分享数

    @Schema(description = "创建时间")
    private LocalDateTime createdAt; // 创建时间

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt; // 更新时间

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt; // 发布时间
    
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
