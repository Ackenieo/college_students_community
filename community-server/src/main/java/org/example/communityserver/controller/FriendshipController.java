package org.example.communityserver.controller;

import org.example.communityserver.dto.FriendshipRequest;
import org.example.communityserver.entity.Friendship;
import org.example.communityserver.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = "*")
public class FriendshipController {
    
    @Autowired
    private FriendshipService friendshipService;
    
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody FriendshipRequest request) {
        try {
            Friendship friendship = friendshipService.sendFriendRequest(userId, request.getFriendId());
            return ResponseEntity.status(HttpStatus.CREATED).body(friendship);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("发送好友申请失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/respond/{friendId}")
    public ResponseEntity<?> respondToFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String status) {
        try {
            Friendship.FriendshipStatus friendshipStatus = Friendship.FriendshipStatus.valueOf(status.toUpperCase());
            Friendship friendship = friendshipService.respondToFriendRequest(userId, friendId, friendshipStatus);
            return ResponseEntity.ok(friendship);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("处理好友申请失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getFriends(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Friendship> friends = friendshipService.getFriends(userId, page, size);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取好友列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Friendship> pendingRequests = friendshipService.getPendingRequests(userId, page, size);
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取待确认申请失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sent")
    public ResponseEntity<?> getSentRequests(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            var sentRequests = friendshipService.getSentRequests(userId);
            return ResponseEntity.ok(sentRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取已发送申请失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{friendId}")
    public ResponseEntity<?> checkFriendshipStatus(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFriend = friendshipService.isFriend(userId, friendId);
            return ResponseEntity.ok(isFriend);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("检查好友状态失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            friendshipService.removeFriend(userId, friendId);
            return ResponseEntity.ok("好友关系已删除");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("删除好友失败: " + e.getMessage());
        }
    }
}
