package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.entity.Like;
import org.example.communityserver.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞管理控制器
 * 提供帖子、评论等内容的点赞功能
 */
@RestController
@RequestMapping("/likes")
@CrossOrigin(origins = "*")
public class LikeController {
    
    @Autowired
    private LikeService likeService;
    
    /**
     * 切换点赞状态（点赞/取消点赞）
     * @param userId 用户ID（从请求头获取）
     * @param targetId 目标对象ID（帖子ID或评论ID）
     * @param targetType 目标类型（POST-帖子，COMMENT-评论）
     * @return 包含点赞状态和点赞数的响应
     */
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggleLike(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String targetId,
            @RequestParam String targetType) {
        try {
            Like.LikeType type = Like.LikeType.valueOf(targetType.toUpperCase());
            boolean isLiked = likeService.toggleLike(userId, targetId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeService.getLikeCount(targetId, type));
            
            return Result.success("操作成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取点赞状态
     * @param userId 用户ID（从请求头获取）
     * @param targetId 目标对象ID（帖子ID或评论ID）
     * @param targetType 目标类型（POST-帖子，COMMENT-评论）
     * @return 包含用户点赞状态和总点赞数的响应
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getLikeStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String targetId,
            @RequestParam String targetType) {
        try {
            Like.LikeType type = Like.LikeType.valueOf(targetType.toUpperCase());
            boolean isLiked = likeService.isLiked(userId, targetId, type);
            long likeCount = likeService.getLikeCount(targetId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            
            return Result.success("获取状态成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取状态失败: " + e.getMessage());
        }
    }
}
