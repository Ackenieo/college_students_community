package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.communityserver.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子数据传输对象（DTO）
 * 用于在帖子相关操作中传递帖子数据，如创建、更新、查询帖子等。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "帖子数据传输对象")
public class PostDTO {

    @Schema(description = "帖子ID")
    private String id;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者用户名")
    private String authorUsername;

    @Schema(description = "作者邮箱")
    private String authorEmail;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "帖子图片列表")
    private List<String> images;

    @Schema(description = "帖子标签列表")
    private List<String> tags;

    @Schema(description = "帖子状态")
    private Post.PostStatus status;

    @Schema(description = "审核结果")
    private String reviewResult;

    @Schema(description = "审核拒绝原因")
    private String reviewReason;

    @Schema(description = "点赞数量")
    private Integer likeCount;

    @Schema(description = "评论数量")
    private Integer commentCount;

    @Schema(description = "分享数量")
    private Integer shareCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked; // 当前用户是否已点赞

    @Schema(description = "当前用户是否已收藏")
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
