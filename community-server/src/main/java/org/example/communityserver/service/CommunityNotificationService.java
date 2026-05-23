package org.example.communityserver.service;

import lombok.extern.slf4j.Slf4j;
import org.example.communityserver.dto.NotificationRequest;
import org.example.communityserver.remote.NotificationRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CommunityNotificationService {

    @Autowired
    private NotificationRemoteService notificationRemoteService;

    public void sendFriendRequestNotification(Long receiverId, Long senderId, String senderUsername, String requestMessage) {
        Map<String, Object> data = baseData(senderId, senderUsername);
        data.put("requestMessage", requestMessage);
        send(build(receiverId, "好友申请", senderUsername + " 请求添加你为好友", "FRIEND_REQUEST", "friend", data, "/friends/pending"));
    }

    public void sendFriendAcceptedNotification(Long requesterId, Long accepterId, String accepterUsername) {
        Map<String, Object> data = baseData(accepterId, accepterUsername);
        send(build(requesterId, "好友申请已通过", accepterUsername + " 已通过你的好友申请", "FRIEND_REQUEST", "friend", data, "/friends"));
    }

    public void sendMessageNotification(Long receiverId, Long senderId, String senderUsername, String content) {
        Map<String, Object> data = baseData(senderId, senderUsername);
        data.put("preview", preview(content));
        send(build(receiverId, "收到新私信", senderUsername + " 给你发送了新消息", "MESSAGE", "message", data, "/messages"));
    }

    public void sendPostCommentNotification(Long receiverId, Long commenterId, String commenterUsername, String postId, String commentId, String content) {
        Map<String, Object> data = baseData(commenterId, commenterUsername);
        data.put("postId", postId);
        data.put("commentId", commentId);
        data.put("preview", preview(content));
        send(build(receiverId, "收到新评论", commenterUsername + " 评论了你的帖子", "COMMENT", "comment", data, "/posts/" + postId));
    }

    public void sendPostLikeNotification(Long receiverId, Long likerId, String targetId) {
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", likerId);
        data.put("targetType", "POST");
        data.put("targetId", targetId);
        send(build(receiverId, "收到新点赞", "有人点赞了你的帖子", "LIKE", "like", data, "/posts/" + targetId));
    }

    public void sendCommentLikeNotification(Long receiverId, Long likerId, String commentId, String postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", likerId);
        data.put("targetType", "COMMENT");
        data.put("targetId", commentId);
        data.put("postId", postId);
        send(build(receiverId, "收到新点赞", "有人点赞了你的评论", "LIKE", "like", data, "/posts/" + postId));
    }

    private void send(NotificationRequest request) {
        try {
            notificationRemoteService.createNotification(request);
        } catch (Exception e) {
            log.warn("创建通知失败: receiverId={}, type={}, category={}", request.getReceiverId(), request.getType(), request.getCategory(), e);
        }
    }

    private NotificationRequest build(Long receiverId, String title, String content, String type, String category, Map<String, Object> data, String actionUrl) {
        NotificationRequest request = new NotificationRequest();
        request.setReceiverId(receiverId);
        request.setTitle(title);
        request.setContent(content);
        request.setType(type);
        request.setPriority("NORMAL");
        request.setCategory(category);
        request.setData(data);
        request.setActionUrl(actionUrl);
        return request;
    }

    private Map<String, Object> baseData(Long senderId, String senderUsername) {
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", senderId);
        data.put("senderUsername", senderUsername);
        return data;
    }

    private String preview(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        return content.length() > 50 ? content.substring(0, 50) : content;
    }
}
