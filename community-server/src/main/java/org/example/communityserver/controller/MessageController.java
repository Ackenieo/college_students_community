package org.example.communityserver.controller;

import org.example.communityserver.dto.MessageRequest;
import org.example.communityserver.entity.Message;
import org.example.communityserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody MessageRequest request) {
        try {
            Message message = messageService.sendMessage(userId, username, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("发送消息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/conversation/{userId2}")
    public ResponseEntity<?> getConversation(
            @PathVariable Long userId2,
            @RequestHeader("X-User-Id") Long userId1,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Message> conversation = messageService.getConversation(userId1, userId2, page, size);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取对话失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadMessages(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var unreadMessages = messageService.getUnreadMessages(userId);
            long unreadCount = messageService.getUnreadCount(userId);
            
            var response = new java.util.HashMap<String, Object>();
            response.put("messages", unreadMessages);
            response.put("count", unreadCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取未读消息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Message> messages = messageService.getUserMessages(userId, page, size);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取消息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            long count = messageService.getUnreadCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取未读数量失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/unread-count/{senderId}")
    public ResponseEntity<?> getUnreadCountBetweenUsers(
            @PathVariable Long senderId,
            @RequestHeader("X-User-Id") Long receiverId) {
        try {
            long count = messageService.getUnreadCountBetweenUsers(senderId, receiverId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取未读数量失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            messageService.markAsRead(messageId, userId);
            return ResponseEntity.ok("消息已标记为已读");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("标记已读失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/conversation/{userId2}/read")
    public ResponseEntity<?> markConversationAsRead(
            @PathVariable Long userId2,
            @RequestHeader("X-User-Id") Long userId1) {
        try {
            messageService.markConversationAsRead(userId1, userId2, userId1);
            return ResponseEntity.ok("对话已标记为已读");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("标记对话已读失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentConversations(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var conversations = messageService.getRecentConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取最近对话失败: " + e.getMessage());
        }
    }
}
