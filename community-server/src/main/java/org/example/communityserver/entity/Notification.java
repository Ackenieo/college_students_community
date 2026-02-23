package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_receiver_id", columnList = "receiver_id"),
    @Index(name = "idx_is_read", columnList = "is_read"),
    @Index(name = "idx_receiver_read", columnList = "receiver_id,is_read")
})
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "通知实体")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "通知ID")
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    @Schema(description = "接收者ID")
    private Long receiverId;

    @Column(name = "sender_id")
    @Schema(description = "发送者ID")
    private Long senderId;

    @Column(name = "sender_nickname", length = 100)
    @Schema(description = "发送者昵称")
    private String senderNickname;

    @Column(name = "sender_avatar", length = 500)
    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Column(name = "notification_type", nullable = false, length = 50)
    @Schema(description = "通知类型")
    private String notificationType;

    @Column(name = "content", columnDefinition = "TEXT")
    @Schema(description = "通知内容")
    private String content;

    @Column(name = "is_read", nullable = false)
    @Schema(description = "是否已读")
    private Boolean isRead;

    @Column(name = "target_id")
    @Schema(description = "目标ID(帖子ID、评论ID等)")
    private String targetId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
