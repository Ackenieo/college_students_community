package org.example.communityserver.repository;

import org.example.communityserver.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    // 查找两个用户之间的对话
    @Query("{$or: [{'senderId': ?0, 'receiverId': ?1}, {'senderId': ?1, 'receiverId': ?0}]}")
    Page<Message> findConversationBetweenUsers(Long userId1, Long userId2, Pageable pageable);
    
    // 查找用户的未读消息
    @Query("{'receiverId': ?0, 'status': 'SENT'}")
    List<Message> findUnreadMessagesByReceiverId(Long receiverId);
    
    // 查找用户的所有对话
    @Query("{$or: [{'senderId': ?0}, {'receiverId': ?0}]}")
    Page<Message> findMessagesByUserId(Long userId, Pageable pageable);
    
    // 统计未读消息数量
    @Query("{'receiverId': ?0, 'status': 'SENT'}")
    long countUnreadMessagesByReceiverId(Long receiverId);
    
    // 查找与特定用户的未读消息数量
    @Query("{'senderId': ?0, 'receiverId': ?1, 'status': 'SENT'}")
    long countUnreadMessagesBetweenUsers(Long senderId, Long receiverId);
    
    // 查找最近的对话列表
    @Query("{$or: [{'senderId': ?0}, {'receiverId': ?0}]}")
    List<Message> findRecentConversationsByUserId(Long userId);
    
    // 根据ID和接收者ID查找消息
    Optional<Message> findByIdAndReceiverId(String id, Long receiverId);
}
