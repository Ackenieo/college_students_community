package org.example.communityserver.service;

import org.example.communityserver.dto.CreatePostRequest;
import org.example.communityserver.dto.PostDTO;
import org.example.communityserver.entity.Post;
import org.example.communityserver.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private AgentService agentService; // 用于内容审核
    
    public PostDTO createPost(Long authorId, String authorUsername, String authorEmail, CreatePostRequest request) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setAuthorUsername(authorUsername);
        post.setAuthorEmail(authorEmail);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        post.setTags(request.getTags());
        post.setStatus(Post.PostStatus.PENDING); // 待审核
        
        Post savedPost = postRepository.save(post);
        
        // 异步进行内容审核
        reviewPostContent(savedPost);
        
        return PostDTO.fromEntity(savedPost);
    }
    
    public PostDTO getPostById(String postId, Long currentUserId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        
        Post post = postOpt.get();
        PostDTO dto = PostDTO.fromEntity(post);
        
        // 设置用户交互状态
        setUserInteractionStatus(dto, currentUserId);
        
        return dto;
    }
    
    public Page<PostDTO> getPostsByStatus(Post.PostStatus status, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        
        return posts.map(post -> {
            PostDTO dto = PostDTO.fromEntity(post);
            setUserInteractionStatus(dto, currentUserId);
            return dto;
        });
    }
    
    public Page<PostDTO> getApprovedPosts(int page, int size, Long currentUserId) {
        return getPostsByStatus(Post.PostStatus.APPROVED, page, size, currentUserId);
    }
    
    public Page<PostDTO> getUserPosts(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
        
        return posts.map(post -> {
            PostDTO dto = PostDTO.fromEntity(post);
            setUserInteractionStatus(dto, currentUserId);
            return dto;
        });
    }
    
    public Page<PostDTO> searchPosts(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.searchPosts(keyword, pageable);
        
        return posts.map(post -> {
            PostDTO dto = PostDTO.fromEntity(post);
            setUserInteractionStatus(dto, currentUserId);
            return dto;
        });
    }
    
    public Page<PostDTO> getHotPosts(int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findApprovedPostsOrderByLikeCountDesc(pageable);
        
        return posts.map(post -> {
            PostDTO dto = PostDTO.fromEntity(post);
            setUserInteractionStatus(dto, currentUserId);
            return dto;
        });
    }
    
    public PostDTO updatePost(String postId, Long authorId, CreatePostRequest request) {
        Optional<Post> postOpt = postRepository.findByIdAndAuthorId(postId, authorId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("帖子不存在或无权限修改");
        }
        
        Post post = postOpt.get();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        post.setTags(request.getTags());
        post.setStatus(Post.PostStatus.PENDING); // 重新提交审核
        
        Post savedPost = postRepository.save(post);
        
        // 重新进行内容审核
        reviewPostContent(savedPost);
        
        return PostDTO.fromEntity(savedPost);
    }
    
    public void deletePost(String postId, Long authorId) {
        Optional<Post> postOpt = postRepository.findByIdAndAuthorId(postId, authorId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("帖子不存在或无权限删除");
        }
        
        Post post = postOpt.get();
        post.setStatus(Post.PostStatus.DELETED);
        postRepository.save(post);
    }
    
    private void setUserInteractionStatus(PostDTO dto, Long currentUserId) {
        // TODO: 实现用户交互状态设置
        // 检查当前用户是否已点赞、收藏该帖子
        dto.setIsLiked(false);
        dto.setIsFavorited(false);
    }
    
    private void reviewPostContent(Post post) {
        try {
            // 调用Agent服务进行内容审核
            String reviewResult = agentService.reviewContent(post.getContent());
            
            if ("APPROVED".equals(reviewResult)) {
                post.setStatus(Post.PostStatus.APPROVED);
                post.setPublishedAt(LocalDateTime.now());
                post.setReviewResult("APPROVED");
            } else if ("REJECTED".equals(reviewResult)) {
                post.setStatus(Post.PostStatus.REJECTED);
                post.setReviewResult("REJECTED");
                post.setReviewReason("内容不符合社区规范");
            } else {
                // PENDING - 需要人工审核
                post.setStatus(Post.PostStatus.PENDING);
                post.setReviewResult("PENDING");
                post.setReviewReason("内容需要人工审核");
            }
            
            postRepository.save(post);
        } catch (Exception e) {
            // 审核失败，标记为待审核
            post.setStatus(Post.PostStatus.PENDING);
            post.setReviewResult("PENDING");
            post.setReviewReason("审核服务暂时不可用");
            postRepository.save(post);
        }
    }
    
    public void updatePostStats(String postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            // TODO: 更新帖子统计数据（点赞数、评论数等）
            postRepository.save(post);
        }
    }
}
