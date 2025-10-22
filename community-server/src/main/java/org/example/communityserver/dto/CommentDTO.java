package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.communityserver.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论数据传输对象（DTO）
 * 用于在帖子详情页展示评论信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论数据传输对象")
public class CommentDTO {

    @Schema(description = "评论ID")
    private String id; // 评论ID

    @Schema(description = "帖子ID")
    private String postId; // 帖子ID

    @Schema(description = "作者ID")
    private Long authorId; // 作者ID

    @Schema(description = "作者用户名")
    private String authorUsername; // 作者用户名

    @Schema(description = "评论内容")
    private String content; // 评论内容

    @Schema(description = "父评论ID")
    private String parentCommentId; // 父评论ID

    @Schema(description = "点赞数量")
    private Integer likeCount; // 点赞数量

    @Schema(description = "创建时间")
    private LocalDateTime createdAt; // 创建时间

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt; // 最后更新时间

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked; // 当前用户是否已点赞

    @Schema(description = "回复列表")
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
