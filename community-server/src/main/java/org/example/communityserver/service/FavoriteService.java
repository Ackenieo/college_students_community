package org.example.communityserver.service;

import org.example.communityserver.entity.Favorite;
import org.example.communityserver.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private PostService postService;
    
    public boolean toggleFavorite(Long userId, String postId) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndPostId(userId, postId);
        
        if (existingFavorite.isPresent()) {
            // 已收藏，取消收藏
            favoriteRepository.delete(existingFavorite.get());
            return false;
        } else {
            // 未收藏，添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setPostId(postId);
            
            favoriteRepository.save(favorite);
            return true;
        }
    }
    
    public boolean isFavorited(Long userId, String postId) {
        return favoriteRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }
    
    public Page<Favorite> getUserFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public long getFavoriteCount(String postId) {
        return favoriteRepository.countByPostId(postId);
    }
}
