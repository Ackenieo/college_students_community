package org.example.communityserver.repository;

import org.example.communityserver.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // 根据作者ID查找帖子
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // 根据状态查找帖子
    Page<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status, Pageable pageable);
    
    // 根据标签查找帖子
    @Query("{'tags': {$in: ?0}, 'status': 'APPROVED'}")
    Page<Post> findByTagsInAndStatusApproved(List<String> tags, Pageable pageable);
    
    // 搜索帖子（标题和内容）
    @Query("{$and: [{'status': 'APPROVED'}, {$or: [{'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}}]}]}")
    Page<Post> searchPosts(String keyword, Pageable pageable);
    
    // 查找热门帖子
    @Query("{'status': 'APPROVED'}")
    Page<Post> findApprovedPosts(Pageable pageable);
    
    // 根据ID和作者ID查找帖子
    Optional<Post> findByIdAndAuthorId(String id, Long authorId);
    
    // 统计用户帖子数量
    long countByAuthorId(Long authorId);
    
    // 统计用户点赞总数
    @Query("{'authorId': ?0}")
    List<Post> findByAuthorId(Long authorId);
}
