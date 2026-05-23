package org.example.notificationserver.repository;

import org.example.notificationserver.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // 根据接收者ID查找通知
    @Query("{'receiverId': ?0, 'status': {$ne: 'DELETED'}, $or: [{'expireAt': null}, {'expireAt': {$gt: ?1}}]}")
    Page<Notification> findActiveByReceiverId(Long receiverId, LocalDateTime now, Pageable pageable);

    // 根据接收者ID和状态查找通知
    @Query("{'receiverId': ?0, 'status': ?1, $or: [{'expireAt': null}, {'expireAt': {$gt: ?2}}]}")
    List<Notification> findActiveByReceiverIdAndStatus(Long receiverId, Notification.NotificationStatus status, LocalDateTime now);
    
    // 根据ID和接收者ID查找通知
    Optional<Notification> findByIdAndReceiverId(String id, Long receiverId);
    
    // 统计未读通知数量
    @Query(value = "{'receiverId': ?0, 'status': 'UNREAD', $or: [{'expireAt': null}, {'expireAt': {$gt: ?1}}]}", count = true)
    long countActiveUnreadByReceiverId(Long receiverId, LocalDateTime now);
    
    // 查找过期的通知
    @Query("{'expireAt': {$lt: ?0}, 'status': {$ne: 'DELETED'}}")
    List<Notification> findByExpireAtBeforeAndStatusNotDeleted(LocalDateTime expireTime);
    
    // 根据类型查找通知
    Page<Notification> findByReceiverIdAndTypeOrderByCreatedAtDesc(Long receiverId, Notification.NotificationType type, Pageable pageable);
    
    // 根据优先级查找通知
    Page<Notification> findByReceiverIdAndPriorityOrderByCreatedAtDesc(Long receiverId, Notification.NotificationPriority priority, Pageable pageable);
    
    // 根据分类查找通知
    Page<Notification> findByReceiverIdAndCategoryOrderByCreatedAtDesc(Long receiverId, String category, Pageable pageable);
}
