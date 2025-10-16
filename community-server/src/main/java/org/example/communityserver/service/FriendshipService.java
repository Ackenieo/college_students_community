package org.example.communityserver.service;

import org.example.communityserver.dto.FriendshipRequest;
import org.example.communityserver.entity.Friendship;
import org.example.communityserver.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendshipService {
    
    @Autowired
    private FriendshipRepository friendshipRepository;
    
    @Autowired
    private NotificationService notificationService; // 用于发送好友申请通知
    
    public Friendship sendFriendRequest(Long userId, Long friendId) {
        // 检查是否已经是好友
        if (friendshipRepository.checkFriendship(userId, friendId).isPresent()) {
            throw new RuntimeException("已经是好友关系");
        }
        
        // 检查是否已经发送过申请
        Optional<Friendship> existingRequest = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (existingRequest.isPresent()) {
            throw new RuntimeException("已经发送过好友申请");
        }
        
        // 创建好友申请
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        
        Friendship savedFriendship = friendshipRepository.save(friendship);
        
        // TODO: 发送通知给被申请者
        // notificationService.sendFriendRequestNotification(friendId, userId);
        
        return savedFriendship;
    }
    
    public Friendship respondToFriendRequest(Long userId, Long friendId, Friendship.FriendshipStatus status) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findByUserIdAndFriendId(friendId, userId);
        if (friendshipOpt.isEmpty()) {
            throw new RuntimeException("好友申请不存在");
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
