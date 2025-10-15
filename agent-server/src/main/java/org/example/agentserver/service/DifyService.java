package org.example.agentserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.agentserver.config.DifyConfig;
import org.example.agentserver.dto.DifyRequest;
import org.example.agentserver.dto.DifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(DifyService.class);
    
    @Autowired
    private WebClient difyWebClient;
    
    @Autowired
    private DifyConfig difyConfig;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String CONVERSATION_CACHE_PREFIX = "dify:conversation:";
    private static final int CACHE_EXPIRE_HOURS = 24;
    
    public DifyResponse chat(DifyRequest request) {
        try {
            logger.info("调用Dify API，用户ID: {}, 查询: {}", request.getUserId(), request.getQuery());
            
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(request);
            
            // 调用Dify API
            DifyResponse response = difyWebClient.post()
                    .uri("/chat-messages")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(DifyResponse.class)
                    .timeout(Duration.ofMillis(difyConfig.getApi().getTimeout()))
                    .block();
            
            if (response != null) {
                // 缓存对话ID
                if (response.getConversationId() != null) {
                    cacheConversationId(request.getUserId(), response.getConversationId());
                }
                
                logger.info("Dify API调用成功，对话ID: {}", response.getConversationId());
            }
            
            return response;
            
        } catch (WebClientResponseException e) {
            logger.error("Dify API调用失败: {}", e.getResponseBodyAsString(), e);
            return createErrorResponse("API调用失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("调用Dify API时发生异常", e);
            return createErrorResponse("服务异常: " + e.getMessage());
        }
    }
    
    public DifyResponse getConversationHistory(String userId, String conversationId) {
        try {
            logger.info("获取对话历史，用户ID: {}, 对话ID: {}", userId, conversationId);
            
            DifyResponse response = difyWebClient.get()
                    .uri("/messages?conversation_id={conversationId}&user={userId}", 
                         conversationId, userId)
                    .retrieve()
                    .bodyToMono(DifyResponse.class)
                    .timeout(Duration.ofMillis(difyConfig.getApi().getTimeout()))
                    .block();
            
            return response;
            
        } catch (WebClientResponseException e) {
            logger.error("获取对话历史失败: {}", e.getResponseBodyAsString(), e);
            return createErrorResponse("获取对话历史失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("获取对话历史时发生异常", e);
            return createErrorResponse("服务异常: " + e.getMessage());
        }
    }
    
    private Map<String, Object> buildRequestBody(DifyRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("inputs", request.getInputs() != null ? request.getInputs() : new HashMap<>());
        body.put("query", request.getQuery());
        body.put("response_mode", request.getResponseMode());
        body.put("user", request.getUser());
        
        if (request.getConversationId() != null) {
            body.put("conversation_id", request.getConversationId());
        }
        
        if (request.getMetadata() != null) {
            body.put("metadata", request.getMetadata());
        }
        
        return body;
    }
    
    private void cacheConversationId(String userId, String conversationId) {
        try {
            String key = CONVERSATION_CACHE_PREFIX + userId;
            redisTemplate.opsForValue().set(key, conversationId, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.warn("缓存对话ID失败", e);
        }
    }
    
    public String getCachedConversationId(String userId) {
        try {
            String key = CONVERSATION_CACHE_PREFIX + userId;
            Object conversationId = redisTemplate.opsForValue().get(key);
            return conversationId != null ? conversationId.toString() : null;
        } catch (Exception e) {
            logger.warn("获取缓存的对话ID失败", e);
            return null;
        }
    }
    
    private DifyResponse createErrorResponse(String error) {
        DifyResponse response = new DifyResponse();
        response.setError(error);
        response.setStatus("error");
        return response;
    }
}
