package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.CommentDTO;
import org.example.communityserver.dto.CreateCommentRequest;
import org.example.communityserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @PostMapping("/post/{postId}")
    public Result<CommentDTO> createComment(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody CreateCommentRequest request) {
        try {
            CommentDTO comment = commentService.createComment(postId, userId, username, request);
            return Result.success("评论创建成功", comment);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "创建评论失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/post/{postId}")
    public Result<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<CommentDTO> comments = commentService.getCommentsByPostId(postId, page, size, currentUserId);
            return Result.success("获取评论成功", comments);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取评论失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/post/{postId}/top")
    public Result<Page<CommentDTO>> getTopLevelComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<CommentDTO> comments = commentService.getTopLevelComments(postId, page, size, currentUserId);
            return Result.success("获取评论成功", comments);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取评论失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<CommentDTO> updateComment(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCommentRequest request) {
        try {
            CommentDTO comment = commentService.updateComment(id, userId, request);
            return Result.success("评论更新成功", comment);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "更新评论失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            commentService.deleteComment(id, userId);
            return Result.success("评论删除成功", "评论已删除");
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "删除评论失败: " + e.getMessage());
        }
    }
}
