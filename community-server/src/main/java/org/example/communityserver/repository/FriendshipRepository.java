package org.example.communityserver.repository;

import org.example.communityserver.entity.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    
    // 查找好友关系
    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
    
    // 查找用户的所有好友
    @Query("{'userId': ?0, 'status': 'ACCEPTED'}")
    Page<Friendship> findFriendsByUserId(Long userId, Pageable pageable);
    
    // 查找待确认的好友申请
    @Query("{'friendId': ?0, 'status': 'PENDING'}")
    Page<Friendship> findPendingRequestsByUserId(Long userId, Pageable pageable);
    
    // 查找用户发送的好友申请
    @Query("{'userId': ?0, 'status': 'PENDING'}")
    List<Friendship> findSentRequestsByUserId(Long userId);
    
    // 统计好友数量
    @Query("{'userId': ?0, 'status': 'ACCEPTED'}")
    long countFriendsByUserId(Long userId);
    
    // 统计待确认的好友申请数量
    @Query("{'friendId': ?0, 'status': 'PENDING'}")
    long countPendingRequestsByUserId(Long userId);
    
    // 检查是否为好友
    @Query("{'userId': ?0, 'friendId': ?1, 'status': 'ACCEPTED'}")
    Optional<Friendship> checkFriendship(Long userId, Long friendId);
}
