package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.FriendshipRequest;
import org.example.communityserver.entity.Friendship;
import org.example.communityserver.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 好友关系管理控制器
 * 提供好友申请、好友关系管理等功能
 */
@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = "*")
public class FriendshipController {
    
    @Autowired
    private FriendshipService friendshipService;

    //先前版本
//    /**
//     * 发送好友申请
//     * 向指定用户发送好友申请。
//     *
//     * @param userId 当前用户ID (从请求头中获取)
//     * @param request 好友申请请求，包含目标好友ID
//     * @return 创建的好友关系对象
//     */
//    @PostMapping("/request")
//    public Result<Friendship> sendFriendRequest(
//            @RequestHeader("X-User-Id") Long userId,
//            @Valid @RequestBody FriendshipRequest request) {
//        try {
//            Friendship friendship = friendshipService.sendFriendRequest(userId, request.getFriendId());
//            return Result.success("好友申请发送成功", friendship);
//        } catch (Exception e) {
//            return Result.error(ResultCode.BAD_REQUEST.getCode(), "发送好友申请失败: " + e.getMessage());
//        }
//    }

    @PostMapping("/request")
    public Result<Friendship> sendFriendRequest(@Valid @RequestBody FriendshipRequest request) {
        try {
            Friendship friendship = friendshipService.sendFriendRequest(request);
            return Result.success("好友申请发送成功", friendship);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "发送好友申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 响应好友申请
     * 接受或拒绝收到的好友申请。
     *
     * @param friendId 好友ID
     * @param userId 当前用户ID (从请求头中获取)
     * @param status 响应状态 (ACCEPTED/REJECTED)
     * @return 更新后的好友关系对象
     */
    @PutMapping("/respond/{friendId}")
    public Result<Friendship> respondToFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String status) {
        try {
            Friendship.FriendshipStatus friendshipStatus = Friendship.FriendshipStatus.valueOf(status.toUpperCase());
            Friendship friendship = friendshipService.respondToFriendRequest(userId, friendId, friendshipStatus);
            return Result.success("好友申请处理成功", friendship);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "处理好友申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取好友列表
     * 分页获取当前用户的好友列表。
     *
     * @param userId 用户ID (从请求头中获取)
     * @param page 页码 (默认为0)
     * @param size 每页数量 (默认为10)
     * @return 好友关系分页列表
     */
    @GetMapping
    public Result<Page<Friendship>> getFriends(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Friendship> friends = friendshipService.getFriends(userId, page, size);
            return Result.success("获取好友列表成功", friends);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取好友列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取待确认的好友申请
     * 获取发送给当前用户但尚未处理的好友申请。
     *
     * @param userId 用户ID (从请求头中获取)
     * @param page 页码 (默认为0)
     * @param size 每页数量 (默认为10)
     * @return 待确认的好友申请分页列表
     */
    @GetMapping("/pending")
    public Result<Page<Friendship>> getPendingRequests(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Friendship> pendingRequests = friendshipService.getPendingRequests(userId, page, size);
            return Result.success("获取待确认申请成功", pendingRequests);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取待确认申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取已发送的好友申请
     * 获取当前用户发送但尚未被处理的好友申请。
     *
     * @param userId 用户ID (从请求头中获取)
     * @return 已发送的好友申请列表
     */
    @GetMapping("/sent")
    public Result<Object> getSentRequests(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var sentRequests = friendshipService.getSentRequests(userId);
            return Result.success("获取已发送申请成功", sentRequests);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取已发送申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查好友关系状态
     * 检查两个用户之间是否存在好友关系。
     *
     * @param friendId 好友ID
     * @param userId 当前用户ID (从请求头中获取)
     * @return 是否为好友关系的布尔值
     */
    @GetMapping("/status/{friendId}")
    public Result<Boolean> checkFriendshipStatus(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFriend = friendshipService.isFriend(userId, friendId);
            return Result.success("检查好友状态成功", isFriend);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "检查好友状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除好友关系
     * 删除与指定用户的好友关系。
     *
     * @param friendId 好友ID
     * @param userId 当前用户ID (从请求头中获取)
     * @return 删除操作结果
     */
    @DeleteMapping("/{friendId}")
    public Result<String> removeFriend(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            friendshipService.removeFriend(userId, friendId);
            return Result.success("好友关系删除成功", "好友关系已删除");
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "删除好友失败: " + e.getMessage());
        }
    }
}
