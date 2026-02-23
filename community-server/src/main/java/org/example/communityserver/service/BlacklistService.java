package org.example.communityserver.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.InvalidOperationException;
import org.example.common.result.ResultCode;
import org.example.communityserver.entity.Blacklist;
import org.example.communityserver.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.example.common.constant.CommonConstants.CacheExpire.FRIENDSHIP_CACHE;
import static org.example.common.constant.CommonConstants.CacheKeyPrefix.FRIENDSHIP;

@Slf4j
@Service
@Transactional
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    public Blacklist addToBlacklist(Long userId, Long blockedUserId, String reason) {
        if (userId.equals(blockedUserId)) {
            throw new InvalidOperationException(ResultCode.BAD_REQUEST, "不能将自己加入黑名单");
        }

        String cacheKey = BLACKLIST + userId + ":" + blockedUserId;
        if (redisTemplate.opsForValue().get(cacheKey) != null) {
            throw new InvalidOperationException(ResultCode.FRIENDSHIP_ALREADY_EXISTS, "该用户已在黑名单中");
        }

        if (blacklistRepository.existsByUserIdAndBlockedUserId(userId, blockedUserId)) {
            redisTemplate.opsForValue().set(cacheKey, "exists", FRIENDSHIP_CACHE, TimeUnit.SECONDS);
            throw new InvalidOperationException(ResultCode.FRIENDSHIP_ALREADY_EXISTS, "该用户已在黑名单中");
        }

        Blacklist blacklist = Blacklist.builder()
                .userId(userId)
                .blockedUserId(blockedUserId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Blacklist savedBlacklist = blacklistRepository.save(blacklist);
        redisTemplate.opsForValue().set(cacheKey, "exists", FRIENDSHIP_CACHE, TimeUnit.SECONDS);

        log.info("用户[{}]已将用户[{}]加入黑名单", userId, blockedUserId);
        return savedBlacklist;
    }

    public void removeFromBlacklist(Long userId, Long blockedUserId) {
        String cacheKey = BLACKLIST + userId + ":" + blockedUserId;
        blacklistRepository.deleteByUserIdAndBlockedUserId(userId, blockedUserId);
        redisTemplate.delete(cacheKey);
        log.info("用户[{}]已将用户[{}]从黑名单中移除", userId, blockedUserId);
    }

    public boolean isBlocked(Long userId, Long targetUserId) {
        String cacheKey = BLACKLIST + userId + ":" + targetUserId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return "exists".equals(cached);
        }

        boolean isBlocked = blacklistRepository.existsByUserIdAndBlockedUserId(userId, targetUserId);
        if (isBlocked) {
            redisTemplate.opsForValue().set(cacheKey, "exists", FRIENDSHIP_CACHE, TimeUnit.SECONDS);
        }
        return isBlocked;
    }

    public boolean isEitherBlocked(Long userId1, Long userId2) {
        return isBlocked(userId1, userId2) || isBlocked(userId2, userId1);
    }

    private static final String BLACKLIST = "blacklist:";
}
