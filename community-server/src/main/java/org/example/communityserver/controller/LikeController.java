package org.example.communityserver.controller;

import org.example.communityserver.entity.Like;
import org.example.communityserver.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@CrossOrigin(origins = "*")
public class LikeController {
    
    @Autowired
    private LikeService likeService;
    
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String targetId,
            @RequestParam String targetType) {
        try {
            Like.LikeType type = Like.LikeType.valueOf(targetType.toUpperCase());
            boolean isLiked = likeService.toggleLike(userId, targetId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeService.getLikeCount(targetId, type));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("操作失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getLikeStatus(
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
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取状态失败: " + e.getMessage());
        }
    }
}
