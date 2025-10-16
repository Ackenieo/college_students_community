package org.example.communityserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.communityserver.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    
    private String id;
    
    private Long authorId;
    
    private String authorUsername;
    
    private String authorEmail;
    
    private String title;
    
    private String content;
    
    private List<String> images;
    
    private List<String> tags;
    
    private Post.PostStatus status;
    
    private String reviewResult;
    
    private String reviewReason;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer shareCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;
    
    private Boolean isLiked; // 当前用户是否已点赞
    
    private Boolean isFavorited; // 当前用户是否已收藏
    
    public static PostDTO fromEntity(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorUsername(post.getAuthorUsername());
        dto.setAuthorEmail(post.getAuthorEmail());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImages(post.getImages());
        dto.setTags(post.getTags());
        dto.setStatus(post.getStatus());
        dto.setReviewResult(post.getReviewResult());
        dto.setReviewReason(post.getReviewReason());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setShareCount(post.getShareCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setPublishedAt(post.getPublishedAt());
        return dto;
    }
}
