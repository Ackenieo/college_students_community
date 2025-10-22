package org.example.communityserver.service;

import org.example.communityserver.dto.CommentDTO;
import org.example.communityserver.dto.CreateCommentRequest;
import org.example.communityserver.entity.Comment;
import org.example.communityserver.entity.Like;
import org.example.communityserver.repository.CommentRepository;
import org.example.communityserver.repository.LikeRepository;
import org.example.communityserver.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;

    /**
     * 创建评论
     * @param postId
     * @param authorId
     * @param authorUsername
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentDTO createComment(String postId, Long authorId, String authorUsername, CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setAuthorUsername(authorUsername);
        comment.setContent(request.getContent());
        comment.setParentCommentId(request.getParentCommentId());
        
        Comment savedComment = commentRepository.save(comment);
        
        // TODO: 更新帖子评论数(待测试)
        postRepository.findById(postId).ifPresent(post -> {
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        });

        return CommentDTO.fromEntity(savedComment);
    }
    /**
     * 获取帖子的所有评论（包括回复）
     * @param postId
     * @param page
     * @param size
     * @param currentUserId
     * @return
     */
    public Page<CommentDTO> getCommentsByPostId(String postId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        
        return comments.map(comment -> {
            CommentDTO dto = CommentDTO.fromEntity(comment);
            setUserInteractionStatus(dto, currentUserId);
            
            // 如果是顶级评论，加载回复
            if (comment.getParentCommentId() == null) {
                List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(comment.getId());
                List<CommentDTO> replyDTOs = replies.stream()
                        .map(reply -> {
                            CommentDTO replyDTO = CommentDTO.fromEntity(reply);
                            setUserInteractionStatus(replyDTO, currentUserId);
                            return replyDTO;
                        })
                        .collect(Collectors.toList());
                dto.setReplies(replyDTOs);
            }
            
            return dto;
        });
    }

    /**
     * 获取帖子的顶级评论（没有父评论）
     * @param postId
     * @param page
     * @param size
     * @param currentUserId
     * @return
     */
    public Page<CommentDTO> getTopLevelComments(String postId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageable);
        
        return comments.map(comment -> {
            CommentDTO dto = CommentDTO.fromEntity(comment);
            setUserInteractionStatus(dto, currentUserId);
            
            // 加载回复
            List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(comment.getId());
            List<CommentDTO> replyDTOs = replies.stream()
                    .map(reply -> {
                        CommentDTO replyDTO = CommentDTO.fromEntity(reply);
                        setUserInteractionStatus(replyDTO, currentUserId);
                        return replyDTO;
                    })
                    .collect(Collectors.toList());
            dto.setReplies(replyDTOs);
            
            return dto;
        });
    }
    /**
     * 更新评论内容
     * @param commentId
     * @param authorId
     * @param request
     * @return
     */
    public CommentDTO updateComment(String commentId, Long authorId, CreateCommentRequest request) {
        Optional<Comment> commentOpt = commentRepository.findByIdAndAuthorId(commentId, authorId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("评论不存在或无权限修改");
        }
        
        Comment comment = commentOpt.get();
        comment.setContent(request.getContent());
        
        Comment savedComment = commentRepository.save(comment);
        return CommentDTO.fromEntity(savedComment);
    }

    /**
     * 删除评论
     * @param commentId
     * @param authorId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String commentId, Long authorId) {
        Optional<Comment> commentOpt = commentRepository.findByIdAndAuthorId(commentId, authorId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("评论不存在或无权限删除");
        }
        // 通过评论找到帖子
        Comment comment = commentOpt.get();
        String postId = comment.getPostId();

        commentRepository.deleteById(commentId);
        
        // TODO: 更新帖子评论数(待测试)
        postRepository.findById(postId).ifPresent(post -> {
            post.setCommentCount(post.getCommentCount() - 1);
            postRepository.save(post);
        });

    }

     /**
      * 设置评论的用户交互状态（点赞、回复数等）
      * @param dto
      * @param currentUserId
      */
    private void setUserInteractionStatus(CommentDTO dto, Long currentUserId) {
        // TODO: 实现用户交互状态设置(待测试)
        // 检查当前用户是否已点赞该评论
        dto.setIsLiked(likeRepository.findByUserIdAndTargetIdAndTargetType(currentUserId, dto.getId(), Like.LikeType.COMMENT).isPresent());

    }
}
