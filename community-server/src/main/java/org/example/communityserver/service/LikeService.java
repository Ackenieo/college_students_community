package org.example.communityserver.service;

import org.example.communityserver.entity.Like;
import org.example.communityserver.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LikeService {
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private org.example.communityserver.repository.CommentRepository commentRepository;
    
    public boolean toggleLike(Long userId, String targetId, Like.LikeType targetType) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
        
        if (existingLike.isPresent()) {
            // 已点赞，取消点赞
            likeRepository.delete(existingLike.get());
            updateTargetLikeCount(targetId, targetType, -1);
            return false;
        } else {
            // 未点赞，添加点赞
            Like like = new Like();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            
            likeRepository.save(like);
            updateTargetLikeCount(targetId, targetType, 1);
            return true;
        }
    }
    
    public boolean isLiked(Long userId, String targetId, Like.LikeType targetType) {
        return likeRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType).isPresent();
    }
    
    public long getLikeCount(String targetId, Like.LikeType targetType) {
        return likeRepository.countByTargetIdAndTargetType(targetId, targetType);
    }
    
    private void updateTargetLikeCount(String targetId, Like.LikeType targetType, int delta) {
        if (targetType == Like.LikeType.POST) {
            postService.updatePostStats(targetId);
        } else if (targetType == Like.LikeType.COMMENT) {
            // TODO: 更新评论点赞数 - 已完善: 更新评论点赞数
            commentRepository.findById(targetId).ifPresent(comment -> {
                int newLikeCount = Math.max(0, comment.getLikeCount() + delta);
                comment.setLikeCount(newLikeCount);
                commentRepository.save(comment);
            });
        }
    }
}
