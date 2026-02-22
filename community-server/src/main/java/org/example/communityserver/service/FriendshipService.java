package org.example.communityserver.service;

import org.example.common.dto.UserDTO;
import org.example.common.exception.InvalidOperationException;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.FriendNotificationDTO;
import org.example.communityserver.dto.FriendshipRequest;
import org.example.communityserver.entity.Friendship;
import org.example.communityserver.mq.produce.FriendNotificationProduce;
import org.example.communityserver.mq.produce.FriendRequestExpireProduce;
import org.example.communityserver.remote.UserRemoteService;
import org.example.communityserver.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.example.common.constant.CommonConstants.CacheExpire.FRIENDSHIP_CACHE;
import static org.example.common.constant.CommonConstants.CacheKeyPrefix.FRIENDSHIP;
import static org.example.common.constant.CommonConstants.NotificationType.FRIEND_REQUEST;

@Service
@Transactional
public class FriendshipService {
    
    @Autowired
    private FriendshipRepository friendshipRepository;
    
    @Autowired
    private NotificationService notificationService; // 用于发送好友申请通知

    @Autowired
    private UserRemoteService userRemoteService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private FriendRequestExpireProduce friendRequestExpireProduce;

    @Autowired
    private FriendNotificationProduce friendNotificationProduce;

    //先前版本
//    public Friendship sendFriendRequestV1(Long userId, Long friendId) {
//
//        // 检查是否尝试添加自己为好友
//        if (userId.equals(friendId)) {
//            throw new InvalidOperationException(ResultCode.CANNOT_ADD_SELF_AS_FRIEND, "不能添加自己为好友");
//        }
//
//        // 检查是否已经是好友
//        if (friendshipRepository.checkFriendship(userId, friendId).isPresent()) {
//            throw new InvalidOperationException(ResultCode.FRIEND_REQUEST_ALREADY_EXISTS, "已经是好友关系");
//        }
//
//        // 是否被对方列入黑名单
//        // TODO: 检查是否被对方列入黑名单
//
//        // 检查是否已经发送过申请
//        Optional<Friendship> existingRequest = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
//        if (existingRequest.isPresent()) {
//            throw new InvalidOperationException(ResultCode.FRIEND_REQUEST_ALREADY_EXISTS, "已经发送过好友申请");
//        }
//
//        // 创建好友申请
//        Friendship friendship = new Friendship();
//        friendship.setUserId(userId);
//        friendship.setFriendId(friendId);
//        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
//
//        Friendship savedFriendship = friendshipRepository.save(friendship);
//        // TODO: 延时队列实现申请过时
//
//        // TODO: 发送通知给被申请者
//        //notificationService.sendFriendRequestNotification(friendId, userId);
//
//        return savedFriendship;
//    }

    public Friendship sendFriendRequest(FriendshipRequest request) {
        Long friendId = request.getFriendId();
        Long userId = request.getSenderId();

        // 检查 jwt 中的 username 是否与请求中的 senderName 一致
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(request.getSenderName())) {
            throw new InvalidOperationException(ResultCode.UNAUTHORIZED, "请求发送者用户名与JWT中的用户名不一致");
        }

        // 检查是否尝试添加自己为好友
        if (userId.equals(friendId)) {
            throw new InvalidOperationException(ResultCode.CANNOT_ADD_SELF_AS_FRIEND, "不能添加自己为好友");
        }

        // TODO: 检查好友用户信息是否存在(待测)
        String cacheKeyUserExist = FRIENDSHIP + friendId;
        // 检查缓存中是否存在好友用户信息(防止恶意请求)
        if (redisTemplate.opsForValue().get(cacheKeyUserExist) != null) {
            throw new InvalidOperationException(ResultCode.USER_NOT_FOUND, "好友用户不存在");
        }

        // 检查好友是否存在
        if (userRemoteService.getUserById(friendId) == null) {
            // 缓存好友用户不存在
            redisTemplate.opsForValue().set(cacheKeyUserExist, "notExist", FRIENDSHIP_CACHE, TimeUnit.SECONDS);
            throw new InvalidOperationException(ResultCode.USER_NOT_FOUND, "好友用户不存在");
        }

        // 检查缓存中是否存在好友关系(防止恶意请求)
        String cacheKeyFriendShip = FRIENDSHIP + userId + ":" + friendId;
        if (redisTemplate.opsForValue().get(cacheKeyFriendShip) != null) {
            throw new InvalidOperationException(ResultCode.FRIEND_REQUEST_ALREADY_EXISTS, "已经是好友关系");
        }

        // 检查是否已经是好友
        if (friendshipRepository.checkFriendship(userId, friendId).isPresent()) {
            // 保存到缓存
            redisTemplate.opsForValue().set(cacheKeyFriendShip, "exists", FRIENDSHIP_CACHE, TimeUnit.SECONDS);
            throw new InvalidOperationException(ResultCode.FRIEND_REQUEST_ALREADY_EXISTS, "已经是好友关系");
        }

        // TODO: 检查是否被对方列入黑名单 - 已完善: 使用BlacklistService检查
        if (blacklistService.isBlocked(friendId, userId)) {
            throw new InvalidOperationException(ResultCode.BAD_REQUEST, "您已被对方列入黑名单,无法发送好友申请");
        }

        // 检查是否已经发送过申请
        Optional<Friendship> existingRequest = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (existingRequest.isPresent()) {
            throw new InvalidOperationException(ResultCode.FRIEND_REQUEST_ALREADY_EXISTS, "已经发送过好友申请");
        }

        // 创建好友申请
        Friendship friendship = Friendship.builder()
                        .userId(userId)
                        .friendId(friendId)
                        .status(Friendship.FriendshipStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        Friendship savedFriendship = friendshipRepository.save(friendship);

        // TODO: 延时队列实现申请过时 - 已完善: 使用FriendRequestExpireProduce发送延时消息
        // 好友申请7天后自动过期
        friendRequestExpireProduce.sendFriendRequestExpireMessage(userId, friendId, savedFriendship.getId(), 7 * 24 * 60 * 60 * 1000L);

        // 创建申请通知
        UserDTO sender = userRemoteService.getUserById(userId);
        FriendNotificationDTO friendNotificationDTO = FriendNotificationDTO.builder()
                        .senderId(userId)
                        .notificationType(FRIEND_REQUEST)
                        .requestMessage(request.getRequestMessage())
                        .senderNickname(sender.getNickname())
                        .senderAvatar(sender.getAvatar())
                        .createTime(LocalDateTime.now())
                        .build();

        // TODO: 准备改消息队列异步实现 - 已完善: 使用FriendNotificationProduce异步发送通知
        org.example.communityserver.mq.event.FriendNotificationEvent notificationEvent =
                org.example.communityserver.mq.event.FriendNotificationEvent.builder()
                        .receiverId(friendId)
                        .friendNotificationDTO(friendNotificationDTO)
                        .build();
        friendNotificationProduce.sendFriendNotification(notificationEvent);

        return savedFriendship;
    }
    
    public Friendship respondToFriendRequest(Long userId, Long friendId, Friendship.FriendshipStatus status) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findByUserIdAndFriendId(friendId, userId);
        if (friendshipOpt.isEmpty()) {
            throw new RuntimeException("好友申请不存在");
        }

        // TODO: 检查是否被对方列入黑名单 - 已完善: 使用BlacklistService检查
        if (blacklistService.isEitherBlocked(userId, friendId)) {
            throw new InvalidOperationException(ResultCode.BAD_REQUEST, "双方存在黑名单关系,无法接受好友申请");
        }

        Friendship friendship = friendshipOpt.get();
        friendship.setStatus(status);

        // 如果是接受申请，创建双向好友关系
        if (status == Friendship.FriendshipStatus.ACCEPTED) {
            // 创建反向好友关系
            Friendship reverseFriendship = new Friendship();
            reverseFriendship.setUserId(userId);
            reverseFriendship.setFriendId(friendId);
            reverseFriendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendshipRepository.save(reverseFriendship);
        }

        return friendshipRepository.save(friendship);
    }
    
    public Page<Friendship> getFriends(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return friendshipRepository.findFriendsByUserId(userId, pageable);
    }
    
    public Page<Friendship> getPendingRequests(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return friendshipRepository.findPendingRequestsByUserId(userId, pageable);
    }
    
    public List<Friendship> getSentRequests(Long userId) {
        return friendshipRepository.findSentRequestsByUserId(userId);
    }
    
    public boolean isFriend(Long userId, Long friendId) {
        return friendshipRepository.checkFriendship(userId, friendId).isPresent();
    }
    
    public long getFriendCount(Long userId) {
        return friendshipRepository.countFriendsByUserId(userId);
    }
    
    public long getPendingRequestCount(Long userId) {
        return friendshipRepository.countPendingRequestsByUserId(userId);
    }
    
    public void removeFriend(Long userId, Long friendId) {
        // 删除双向好友关系
        friendshipRepository.findByUserIdAndFriendId(userId, friendId).ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUserIdAndFriendId(friendId, userId).ifPresent(friendshipRepository::delete);
    }
}
