package org.example.communityserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blacklist", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_blocked_user_id", columnList = "blocked_user_id"),
    @Index(name = "idx_user_blocked", columnList = "user_id,blocked_user_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "黑名单实体")
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "黑名单ID")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID")
    private Long userId;

    @Column(name = "blocked_user_id", nullable = false)
    @Schema(description = "被拉黑的用户ID")
    private Long blockedUserId;

    @Column(name = "reason", length = 500)
    @Schema(description = "拉黑原因")
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}