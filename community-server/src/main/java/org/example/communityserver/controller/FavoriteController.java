package org.example.communityserver.controller;

import org.example.communityserver.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/toggle/{postId}")
    public ResponseEntity<?> toggleFavorite(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFavorited = favoriteService.toggleFavorite(userId, postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            response.put("favoriteCount", favoriteService.getFavoriteCount(postId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("操作失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> favorites = favoriteService.getUserFavorites(userId, page, size);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取收藏失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{postId}")
    public ResponseEntity<?> getFavoriteStatus(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFavorited = favoriteService.isFavorited(userId, postId);
            long favoriteCount = favoriteService.getFavoriteCount(postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            response.put("favoriteCount", favoriteCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("获取状态失败: " + e.getMessage());
        }
    }
}
