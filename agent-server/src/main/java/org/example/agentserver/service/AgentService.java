package org.example.agentserver.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.agentserver.dto.AgentResponse;
import org.example.agentserver.dto.DifyRequest;
import org.example.agentserver.dto.DifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    
    @Autowired
    private DifyService difyService;
    
    @CircuitBreaker(name = "difyAgent", fallbackMethod = "fallbackChat")
    @Retry(name = "difyAgent")
    public AgentResponse chat(String userId, String query, String conversationId) {
        logger.info("处理智能体对话请求，用户ID: {}, 查询: {}", userId, query);
        
        // 如果没有提供对话ID，尝试从缓存获取
        if (conversationId == null) {
            conversationId = difyService.getCachedConversationId(userId);
        }
        
        // 构建Dify请求
        DifyRequest difyRequest = new DifyRequest();
        difyRequest.setUserId(userId);
        difyRequest.setQuery(query);
        difyRequest.setConversationId(conversationId);
        difyRequest.setResponseMode("blocking");
        difyRequest.setUser(userId);
        
        // 添加一些元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "college-student-system");
        metadata.put("timestamp", System.currentTimeMillis());
        difyRequest.setMetadata(metadata);
        
        // 调用Dify服务
        DifyResponse difyResponse = difyService.chat(difyRequest);
        
        // 转换为AgentResponse
        if (difyResponse != null && difyResponse.getError() == null) {
            return AgentResponse.success(
                difyResponse.getAnswer() != null ? difyResponse.getAnswer() : difyResponse.getMessage(),
                difyResponse.getConversationId()
            );
        } else {
            return AgentResponse.error(difyResponse != null ? difyResponse.getError() : "未知错误");
        }
    }
    
    @Retry(name = "difyAgent")
    public AgentResponse reviewContent(String content) {
        logger.info("处理内容审核请求，内容长度: {}", content.length());
        
        // 构建内容审核请求
        DifyRequest difyRequest = new DifyRequest();
        difyRequest.setUserId("system");
        difyRequest.setQuery("请审核以下内容是否包含敏感信息或不适当内容：" + content);
        difyRequest.setResponseMode("blocking");
        difyRequest.setUser("system");
        
        // 添加审核相关的元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "content_review");
        metadata.put("timestamp", System.currentTimeMillis());
        difyRequest.setMetadata(metadata);
        
        // 调用Dify服务进行内容审核
        DifyResponse difyResponse = difyService.chat(difyRequest);
        
        // 解析审核结果
        if (difyResponse != null && difyResponse.getError() == null) {
            String result = difyResponse.getAnswer() != null ? difyResponse.getAnswer() : difyResponse.getMessage();
            return parseReviewResult(result);
        } else {
            return AgentResponse.error(difyResponse != null ? difyResponse.getError() : "内容审核失败");
        }
    }
    
    private AgentResponse parseReviewResult(String result) {
        // 解析AI返回的审核结果
        if (result.toLowerCase().contains("通过") || result.toLowerCase().contains("approve") || 
            result.toLowerCase().contains("正常") || result.toLowerCase().contains("safe")) {
            return AgentResponse.success("APPROVED", "内容审核通过");
        } else if (result.toLowerCase().contains("拒绝") || result.toLowerCase().contains("reject") ||
                   result.toLowerCase().contains("敏感") || result.toLowerCase().contains("不当")) {
            return AgentResponse.error("REJECTED: " + result);
        } else {
            // 默认需要人工审核
            return AgentResponse.success("PENDING", "内容需要人工审核");
        }
    }
    
    public AgentResponse getConversationHistory(String userId, String conversationId) {
        logger.info("获取对话历史，用户ID: {}, 对话ID: {}", userId, conversationId);
        
        DifyResponse difyResponse = difyService.getConversationHistory(userId, conversationId);
        
        if (difyResponse != null && difyResponse.getError() == null) {
            return AgentResponse.success(
                difyResponse.getMessage() != null ? difyResponse.getMessage() : "暂无对话历史",
                conversationId
            );
        } else {
            return AgentResponse.error(difyResponse != null ? difyResponse.getError() : "获取对话历史失败");
        }
    }
    
    public AgentResponse fallbackChat(String userId, String query, String conversationId, Exception ex) {
        logger.error("智能体服务降级处理，用户ID: {}, 查询: {}", userId, query, ex);
        
        return AgentResponse.error("智能体服务暂时不可用，请稍后重试");
    }
    
    public AgentResponse fallbackChat(String userId, String query, Exception ex) {
        logger.error("智能体服务降级处理，用户ID: {}, 查询: {}", userId, query, ex);
        
        return AgentResponse.error("智能体服务暂时不可用，请稍后重试");
    }
}
