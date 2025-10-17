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

/**
 * 帖子管理控制器
 * 提供帖子的创建、查询、更新、删除等功能
 */
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 创建新帖子
     * @param userId 用户ID（从请求头获取）
     * @param username 用户名（从请求头获取）
     * @param email 用户邮箱（从请求头获取）
     * @param request 帖子创建请求，包含标题、内容、类型等信息
     * @return 创建成功的帖子信息
     */
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
    
    /**
     * 根据ID获取帖子详情
     * @param id 帖子ID
     * @param currentUserId 当前用户ID（可选，用于判断是否已点赞/收藏）
     * @return 帖子详细信息
     */
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
    
    /**
     * 获取已审核通过的帖子列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞/收藏状态）
     * @return 分页的帖子列表
     */
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
    
    /**
     * 获取指定用户的帖子列表
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞/收藏状态）
     * @return 分页的用户帖子列表
     */
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
    
    /**
     * 搜索帖子
     * @param keyword 搜索关键词（标题或内容）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞/收藏状态）
     * @return 分页的搜索结果
     */
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
    
    /**
     * 获取热门帖子列表（按点赞数和评论数排序）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选，用于判断点赞/收藏状态）
     * @return 分页的热门帖子列表
     */
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
    
    /**
     * 更新帖子内容（仅作者可操作）
     * @param id 帖子ID
     * @param userId 用户ID（从请求头获取，用于权限验证）
     * @param request 更新请求，包含新的帖子内容
     * @return 更新后的帖子信息
     */
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
    
    /**
     * 删除帖子（仅作者可操作）
     * @param id 帖子ID
     * @param userId 用户ID（从请求头获取，用于权限验证）
     * @return 删除结果
     */
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
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常", "Community service is running");
    }
}
