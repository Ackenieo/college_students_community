package org.example.communityserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建评论请求")
public class CreateCommentRequest {

    @Schema(description = "帖子ID")
    @NotBlank(message = "帖子ID不能为空")
    private String postId;

    @Schema(description = "评论内容")
    @Size(max = 5000, message = "评论长度不能超过5000个字符")
    private String content;

    @Schema(description = "父评论ID")
    private String parentCommentId; // 父评论ID，用于回复功能
}
