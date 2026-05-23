package org.example.communityserver.service;

import org.example.common.dto.UserDTO;
import org.example.common.result.Result;
import org.example.communityserver.dto.MessageRequest;
import org.example.communityserver.entity.Message;
import org.example.communityserver.remote.UserRemoteService;
import org.example.communityserver.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private FriendshipService friendshipService; // 用于验证好友关系
    
    @Autowired
    private NotificationService notificationService; // 用于实时通知

    @Autowired
    private UserRemoteService userRemoteService;

    public Message sendMessage(Long senderId, String senderUsername, MessageRequest request) {
        validateMessageTarget(senderId, request);
        if (!friendshipService.isFriend(senderId, request.getReceiverId())) {
            throw new RuntimeException("只能给好友发送消息");
        }
        
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderUsername(senderUsername);
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setMessageType(Message.MessageType.valueOf(request.getMessageType()));
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setStatus(Message.MessageStatus.SENT);
        
        Message savedMessage = messageRepository.save(message);

        // TODO: 发送实时通知 - 已完善: 调用NotificationService发送实时通知
        notificationService.sendMessageNotification(request.getReceiverId(), senderId, senderUsername, request.getContent());

        return savedMessage;
    }
    
    private void validateMessageTarget(Long senderId, MessageRequest request) {
        if (!senderId.equals(request.getSenderId())) {
            throw new RuntimeException("消息发送者与当前用户不一致");
        }
        if (senderId.equals(request.getReceiverId())) {
            throw new RuntimeException("不能给自己发送消息");
        }
        UserDTO receiver = getExistingUser(request.getReceiverId());
        if (receiver == null) {
            throw new RuntimeException("接收者不存在");
        }
    }

    private UserDTO getExistingUser(Long userId) {
        Result<UserDTO> result = userRemoteService.getUserById(userId);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    public Page<Message> getConversation(Long userId1, Long userId2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findConversationBetweenUsers(userId1, userId2, pageable);
    }
    
    public List<Message> getUnreadMessages(Long receiverId) {
        return messageRepository.findUnreadMessagesByReceiverId(receiverId);
    }
    
    public Page<Message> getUserMessages(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findMessagesByUserId(userId, pageable);
    }
    
    public long getUnreadCount(Long receiverId) {
        return messageRepository.countUnreadMessagesByReceiverId(receiverId);
    }
    
    public long getUnreadCountBetweenUsers(Long senderId, Long receiverId) {
        return messageRepository.countUnreadMessagesBetweenUsers(senderId, receiverId);
    }
    
    public void markAsRead(String messageId, Long receiverId) {
        Optional<Message> messageOpt = messageRepository.findByIdAndReceiverId(messageId, receiverId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setStatus(Message.MessageStatus.READ);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
    
    public void markConversationAsRead(Long userId1, Long userId2, Long readerId) {
        // 将对话中所有未读消息标记为已读
        List<Message> unreadMessages = messageRepository.findUnreadMessagesByReceiverId(readerId);
        for (Message message : unreadMessages) {
            if ((message.getSenderId().equals(userId1) && message.getReceiverId().equals(userId2)) ||
                (message.getSenderId().equals(userId2) && message.getReceiverId().equals(userId1))) {
                message.setStatus(Message.MessageStatus.READ);
                message.setReadAt(LocalDateTime.now());
                messageRepository.save(message);
            }
        }
    }
    
    public List<Message> getRecentConversations(Long userId) {
        return messageRepository.findRecentConversationsByUserId(userId);
    }
}
