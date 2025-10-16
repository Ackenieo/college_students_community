package org.example.communityserver.controller;

import org.example.communityserver.dto.CommentDTO;
import org.example.communityserver.dto.CreateCommentRequest;
import org.example.communityserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody CreateCommentRequest request) {
        try {
            CommentDTO comment = commentService.createComment(postId, userId, username, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("创建评论失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<CommentDTO> comments = commentService.getCommentsByPostId(postId, page, size, currentUserId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取评论失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/post/{postId}/top")
    public ResponseEntity<?> getTopLevelComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<CommentDTO> comments = commentService.getTopLevelComments(postId, page, size, currentUserId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取评论失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCommentRequest request) {
        try {
            CommentDTO comment = commentService.updateComment(id, userId, request);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新评论失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            commentService.deleteComment(id, userId);
            return ResponseEntity.ok("评论删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("删除评论失败: " + e.getMessage());
        }
    }
}
