package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.MessageRequest;
import org.example.communityserver.entity.Message;
import org.example.communityserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息管理控制器
 * 提供用户间消息发送、接收、查询等功能
 */
@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 发送消息
     * 向指定用户发送消息。
     *
     * @param userId 发送者用户ID (从请求头中获取)
     * @param username 发送者用户名 (从请求头中获取)
     * @param request 消息请求，包含接收者ID和消息内容
     * @return 发送的消息对象
     */
    @PostMapping("/send")
    public Result<Message> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody MessageRequest request) {
        try {
            Message message = messageService.sendMessage(userId, username, request);
            return Result.success("消息发送成功", message);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取对话记录
     * 获取两个用户之间的对话记录，支持分页。
     *
     * @param userId2 对话对象用户ID
     * @param userId1 当前用户ID (从请求头中获取)
     * @param page 页码 (默认为0)
     * @param size 每页数量 (默认为20)
     * @return 对话消息分页列表
     */
    @GetMapping("/conversation/{userId2}")
    public Result<Page<Message>> getConversation(
            @PathVariable Long userId2,
            @RequestHeader("X-User-Id") Long userId1,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Message> conversation = messageService.getConversation(userId1, userId2, page, size);
            return Result.success("获取对话记录成功", conversation);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息
     * 获取当前用户的所有未读消息及未读消息数量。
     *
     * @param userId 用户ID (从请求头中获取)
     * @return 包含未读消息列表和总数的对象
     */
    @GetMapping("/unread")
    public Result<Map<String, Object>> getUnreadMessages(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var unreadMessages = messageService.getUnreadMessages(userId);
            long unreadCount = messageService.getUnreadCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("messages", unreadMessages);
            response.put("count", unreadCount);
            
            return Result.success("获取未读消息成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取未读消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户消息
     * 分页获取指定用户发送或接收的消息。
     *
     * @param userId 用户ID
     * @param page 页码 (默认为0)
     * @param size 每页数量 (默认为20)
     * @return 用户消息分页列表
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Message>> getUserMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Message> messages = messageService.getUserMessages(userId, page, size);
            return Result.success("获取用户消息成功", messages);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息总数
     * 获取当前用户的未读消息总数。
     *
     * @param userId 用户ID (从请求头中获取)
     * @return 未读消息数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            long count = messageService.getUnreadCount(userId);
            return Result.success("获取未读数量成功", count);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取未读数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取两个用户间的未读消息数量
     * 获取指定发送者和接收者之间的未读消息数量。
     *
     * @param senderId 发送者用户ID
     * @param receiverId 接收者用户ID (从请求头中获取)
     * @return 未读消息数量
     */
    @GetMapping("/unread-count/{senderId}")
    public Result<Long> getUnreadCountBetweenUsers(
            @PathVariable Long senderId,
            @RequestHeader("X-User-Id") Long receiverId) {
        try {
            long count = messageService.getUnreadCountBetweenUsers(senderId, receiverId);
            return Result.success("获取未读数量成功", count);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取未读数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     * 将指定消息标记为已读状态。
     *
     * @param messageId 消息ID
     * @param userId 用户ID (从请求头中获取)
     * @return 操作结果
     */
    @PutMapping("/{messageId}/read")
    public Result<String> markAsRead(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            messageService.markAsRead(messageId, userId);
            return Result.success("消息标记已读成功", "消息已标记为已读");
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "标记已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记整个对话为已读
     * 将两个用户之间的所有未读消息标记为已读。
     *
     * @param userId2 对话对象用户ID
     * @param userId1 当前用户ID (从请求头中获取)
     * @return 操作结果
     */
    @PutMapping("/conversation/{userId2}/read")
    public Result<String> markConversationAsRead(
            @PathVariable Long userId2,
            @RequestHeader("X-User-Id") Long userId1) {
        try {
            messageService.markConversationAsRead(userId1, userId2, userId1);
            return Result.success("对话标记已读成功", "对话已标记为已读");
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "标记对话已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近对话
     * 获取当前用户最近的对话列表。
     *
     * @param userId 用户ID (从请求头中获取)
     * @return 最近对话列表
     */
    @GetMapping("/recent")
    public Result<Object> getRecentConversations(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var conversations = messageService.getRecentConversations(userId);
            return Result.success("获取最近对话成功", conversations);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "获取最近对话失败: " + e.getMessage());
        }
    }
}
