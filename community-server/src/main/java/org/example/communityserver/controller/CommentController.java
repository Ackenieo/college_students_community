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

/**
 * 评论管理控制器
 * 提供帖子的评论功能，包括创建、查询、更新、删除评论
 */
@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 为帖子创建评论
     * @param postId 帖子ID
     * @param userId 评论用户ID（从请求头获取）
     * @param username 评论用户名（从请求头获取）
     * @param request 评论创建请求，包含评论内容和父评论ID（可选）
     * @return 创建成功的评论信息
     */
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
    
    /**
     * 获取指定帖子的所有评论（分页）
     * @param postId 帖子ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞状态）
     * @return 分页的评论列表
     */
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
    
    /**
     * 获取指定帖子的顶级评论（不含回复）
     * @param postId 帖子ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞状态）
     * @return 分页的顶级评论列表
     */
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
    
    /**
     * 更新评论内容（仅评论作者可操作）
     * @param id 评论ID
     * @param userId 用户ID（从请求头获取，用于权限验证）
     * @param request 更新请求，包含新的评论内容
     * @return 更新后的评论信息
     */
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
    
    /**
     * 删除评论（仅评论作者可操作）
     * @param id 评论ID
     * @param userId 用户ID（从请求头获取，用于权限验证）
     * @return 删除结果
     */
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
