package org.example.agentserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.agentserver.dto.AgentResponse;
import org.example.agentserver.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/agent")
@CrossOrigin(origins = "*")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    @PostMapping("/chat")
    public Result<AgentResponse> chat(
            @RequestParam @NotBlank(message = "用户ID不能为空") String userId,
            @RequestParam @NotBlank(message = "查询内容不能为空") String query,
            @RequestParam(required = false) String conversationId) {
        
        try {
            AgentResponse response = agentService.chat(userId, query, conversationId);
            if ("success".equals(response.getStatus())) {
                return Result.success("聊天成功", response);
            } else {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "聊天失败: " + response.getMessage());
            }
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "服务异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/conversation/{conversationId}")
    public Result<AgentResponse> getConversationHistory(
            @PathVariable @NotBlank String conversationId,
            @RequestParam @NotBlank String userId) {
        
        try {
            AgentResponse response = agentService.getConversationHistory(userId, conversationId);
            if ("success".equals(response.getStatus())) {
                return Result.success("获取成功", response);
            } else {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "获取失败: " + response.getMessage());
            }
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "服务异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/review")
    public Result<AgentResponse> reviewContent(
            @RequestParam @NotBlank(message = "内容不能为空") String content) {
        
        try {
            AgentResponse response = agentService.reviewContent(content);
            if ("success".equals(response.getStatus())) {
                return Result.success("内容审核完成", response);
            } else {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "审核失败: " + response.getMessage());
            }
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "服务异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常", "Agent service is running");
    }
}
