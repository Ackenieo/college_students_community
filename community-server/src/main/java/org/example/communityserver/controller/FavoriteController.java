package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏管理控制器
 * 提供帖子的收藏功能，包括收藏/取消收藏、查看收藏列表等
 */
@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 切换（添加/取消）帖子收藏状态
     * 如果帖子未被收藏，则添加收藏；如果已被收藏，则取消收藏。
     *
     * @param postId 帖子ID
     * @param userId 用户ID (从请求头中获取)
     * @return 包含收藏状态和收藏总数的Result对象
     */
    @PostMapping("/toggle/{postId}")
    public Result<Map<String, Object>> toggleFavorite(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFavorited = favoriteService.toggleFavorite(userId, postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            response.put("favoriteCount", favoriteService.getFavoriteCount(postId));
            
            return Result.success("收藏状态更新成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏的帖子列表
     * 支持分页查询。
     *
     * @param userId 用户ID
     * @param page 页码 (默认为0)
     * @param size 每页数量 (默认为10)
     * @return 包含用户收藏帖子分页列表的Result对象
     */
    @GetMapping("/user/{userId}")
    public Result<Page<?>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> favorites = favoriteService.getUserFavorites(userId, page, size);
            return Result.success("查询用户收藏成功", favorites);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "查询用户收藏失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取帖子收藏状态
     * 检查指定用户是否已收藏指定帖子，并返回收藏总数。
     *
     * @param postId 帖子ID
     * @param userId 用户ID (从请求头中获取)
     * @return 包含收藏状态和收藏总数的Result对象
     */
    @GetMapping("/status/{postId}")
    public Result<Map<String, Object>> getFavoriteStatus(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFavorited = favoriteService.isFavorited(userId, postId);
            long favoriteCount = favoriteService.getFavoriteCount(postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            response.put("favoriteCount", favoriteCount);
            
            return Result.success("获取收藏状态成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取收藏状态失败: " + e.getMessage());
        }
    }
}
