package org.example.communityserver.webSocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketService {

    private static final Map<Long, Set<Session>> USER_SESSIONS_MAP = new ConcurrentHashMap<>();

    //private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Long userId; // 当前连接对应的用户ID

    @Getter
    private static WebSocketService instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    /**
     * 连接建立时：给用户的会话集合添加新Session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.userId = userId;
        // 1. 若用户无会话集合，初始化一个空集合
        USER_SESSIONS_MAP.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>());
        // 2. 向用户的会话集合添加当前Session
        Set<Session> userSessions = USER_SESSIONS_MAP.get(userId);
        userSessions.add(session);

        //TODO: 同步通知信息到当前session(消息队列异步实现？)

        log.info("用户[{}]新设备连接，当前会话数={}，总在线用户数={}",
                userId, userSessions.size(), USER_SESSIONS_MAP.size());

    }


    /**
     * 连接关闭时：从用户的会话集合移除当前Session
     */
    @OnClose
    public void onClose(Session session) {
        Set<Session> userSessions = USER_SESSIONS_MAP.get(userId);
        if (userSessions != null) {
            // 移除当前Session
            userSessions.remove(session);
            // 若用户会话集合为空，从Map中删除该用户（避免内存泄漏）
            if (userSessions.isEmpty()) {
                USER_SESSIONS_MAP.remove(userId);
            }
            log.info("用户[{}]设备断开连接，剩余会话数={}，总在线用户数={}",
                    userId, userSessions.size(), USER_SESSIONS_MAP.size());
        }
    }

    /**
     * 连接异常时触发
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") Long userId) {
        log.error("WebSocket连接异常：用户ID={}，错误信息={}", userId, error.getMessage(), error);
        // 异常时自动移除无效连接
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("关闭异常连接失败", e);
            }
        }
        USER_SESSIONS_MAP.get(userId).remove(session);
    }

    /**
     * TODO: 待测
     * 向指定用户的所有在线设备发送消息
     * （解决多设备登录时，所有设备都能收到消息）
     */
    public static boolean sendMessageToUser(Long userId, Object message) {
        if (userId == null || message == null) {
            log.error("发送消息失败：用户ID或消息为空");
            return false;
        }

        Set<Session> userSessions = USER_SESSIONS_MAP.get(userId);
        if (userSessions == null || userSessions.isEmpty()) {
            log.warn("发送消息失败：用户[{}]无在线会话", userId);
            return true;
        }

        boolean allSuccess = true;
        String jsonMessage;
        try {
            //jsonMessage = OBJECT_MAPPER.writeValueAsString(message);
            jsonMessage = JSON.toJSONString(message);
        } catch (Exception e) {
            log.error("消息序列化失败", e);
            return false;
        }

        // 遍历用户的所有会话，逐个发送消息
        for (Session session : userSessions) {
            if (session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(jsonMessage);
                    log.info("向用户[{}]的会话[{}]发送消息成功", userId, session.getId());
                } catch (Exception e) {
                    log.error("向用户[{}]的会话[{}]发送消息失败", userId, session.getId(), e);
                    allSuccess = false; // 只要有一个会话失败，整体标记为失败
                }
            } else {
                // 移除已关闭的会话（清理无效连接）
                userSessions.remove(session);
                log.warn("用户[{}]的会话[{}]已关闭，自动清理", userId, session.getId());
            }
        }
        return allSuccess;
    }

    /**
     * TODO: 待测
     * 判断用户是否在线（是否有至少一个活跃会话）
     */
    public boolean isUserOnline(Long userId) {
        Set<Session> userSessions = USER_SESSIONS_MAP.get(userId);
        if (userSessions == null || userSessions.isEmpty()) {
            return false;
        }
        // 过滤已关闭的会话，判断是否有活跃会话
        return userSessions.stream().anyMatch(Session::isOpen);
    }
}
