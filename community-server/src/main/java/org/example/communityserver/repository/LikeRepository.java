package org.example.communityserver.repository;

import org.example.communityserver.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    
    // 检查用户是否已点赞
    Optional<Like> findByUserIdAndTargetIdAndTargetType(Long userId, String targetId, Like.LikeType targetType);
    
    // 根据用户ID查找点赞记录
    List<Like> findByUserIdAndTargetType(Long userId, Like.LikeType targetType);
    
    // 根据目标ID和类型查找点赞记录
    List<Like> findByTargetIdAndTargetType(String targetId, Like.LikeType targetType);
    
    // 统计目标点赞数量
    long countByTargetIdAndTargetType(String targetId, Like.LikeType targetType);
    
    // 根据用户ID查找点赞的帖子
    @Query("{'userId': ?0, 'targetType': 'POST'}")
    List<Like> findPostLikesByUserId(Long userId);
    
    // 根据用户ID查找点赞的评论
    @Query("{'userId': ?0, 'targetType': 'COMMENT'}")
    List<Like> findCommentLikesByUserId(Long userId);
}
