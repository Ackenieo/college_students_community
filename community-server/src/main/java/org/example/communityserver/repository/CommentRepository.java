package org.example.communityserver.repository;

import org.example.communityserver.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // 根据帖子ID查找评论
    Page<Comment> findByPostIdOrderByCreatedAtDesc(String postId, Pageable pageable);
    
    // 根据作者ID查找评论
    Page<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // 查找顶级评论（没有父评论）
    Page<Comment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(String postId, Pageable pageable);
    
    // 查找回复评论
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);
    
    // 根据ID和作者ID查找评论
    Optional<Comment> findByIdAndAuthorId(String id, Long authorId);
    
    // 统计帖子评论数量
    long countByPostId(String postId);
    
    // 统计用户评论数量
    long countByAuthorId(Long authorId);
}
