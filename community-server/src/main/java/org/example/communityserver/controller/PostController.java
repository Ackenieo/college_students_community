package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.CreatePostRequest;
import org.example.communityserver.dto.PostDTO;
import org.example.communityserver.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @PostMapping
    public Result<PostDTO> createPost(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody CreatePostRequest request) {
        try {
            PostDTO post = postService.createPost(userId, username, email, request);
            return Result.success("帖子创建成功", post);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "创建帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<PostDTO> getPostById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            PostDTO post = postService.getPostById(id, currentUserId);
            return Result.success("查询成功", post);
        } catch (Exception e) {
            return Result.error(ResultCode.POST_NOT_FOUND.getCode(), "帖子不存在: " + e.getMessage());
        }
    }
    
    @GetMapping
    public Result<Page<PostDTO>> getApprovedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<PostDTO> posts = postService.getApprovedPosts(page, size, currentUserId);
            return Result.success("获取帖子成功", posts);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public Result<Page<PostDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<PostDTO> posts = postService.getUserPosts(userId, page, size, currentUserId);
            return Result.success("获取用户帖子成功", posts);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取用户帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public Result<Page<PostDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<PostDTO> posts = postService.searchPosts(keyword, page, size, currentUserId);
            return Result.success("搜索帖子成功", posts);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "搜索帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    public Result<Page<PostDTO>> getHotPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            Page<PostDTO> posts = postService.getHotPosts(page, size, currentUserId);
            return Result.success("获取热门帖子成功", posts);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取热门帖子失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<PostDTO> updatePost(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        try {
            PostDTO post = postService.updatePost(id, userId, request);
            return Result.success("帖子更新成功", post);
        } catch (Exception e) {
            return Result.error(ResultCode.POST_PERMISSION_DENIED.getCode(), "更新帖子失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> deletePost(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            postService.deletePost(id, userId);
            return Result.success("帖子删除成功", "帖子已删除");
        } catch (Exception e) {
            return Result.error(ResultCode.POST_PERMISSION_DENIED.getCode(), "删除帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常", "Community service is running");
    }
}
