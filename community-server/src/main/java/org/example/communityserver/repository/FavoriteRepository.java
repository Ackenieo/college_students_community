package org.example.communityserver.repository;

import org.example.communityserver.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    
    // 检查用户是否已收藏帖子
    Optional<Favorite> findByUserIdAndPostId(Long userId, String postId);
    
    // 根据用户ID查找收藏的帖子
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 根据帖子ID查找收藏记录
    List<Favorite> findByPostId(String postId);
    
    // 统计用户收藏数量
    long countByUserId(Long userId);
    
    // 统计帖子收藏数量
    long countByPostId(String postId);
}
