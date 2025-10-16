package org.example.communityserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.communityserver.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    
    private String id;
    
    private String postId;
    
    private Long authorId;
    
    private String authorUsername;
    
    private String content;
    
    private String parentCommentId;
    
    private Integer likeCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean isLiked; // 当前用户是否已点赞
    
    private List<CommentDTO> replies; // 回复列表
    
    public static CommentDTO fromEntity(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setAuthorId(comment.getAuthorId());
        dto.setAuthorUsername(comment.getAuthorUsername());
        dto.setContent(comment.getContent());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
