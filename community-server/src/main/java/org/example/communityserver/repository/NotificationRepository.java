package org.example.communityserver.repository;

import org.example.communityserver.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    Page<Notification> findByReceiverIdAndIsReadOrderByCreatedAtDesc(Long receiverId, Boolean isRead, Pageable pageable);

    List<Notification> findByReceiverIdAndIsRead(Long receiverId, Boolean isRead);

    long countByReceiverIdAndIsRead(Long receiverId, Boolean isRead);
}
