package org.example.agentserver.controller;

import org.example.agentserver.dto.AgentResponse;
import org.example.agentserver.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/agent")
@CrossOrigin(origins = "*")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(
            @RequestParam @NotBlank(message = "用户ID不能为空") String userId,
            @RequestParam @NotBlank(message = "查询内容不能为空") String query,
            @RequestParam(required = false) String conversationId) {
        
        AgentResponse response = agentService.chat(userId, query, conversationId);
        
        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<AgentResponse> getConversationHistory(
            @PathVariable @NotBlank String conversationId,
            @RequestParam @NotBlank String userId) {
        
        AgentResponse response = agentService.getConversationHistory(userId, conversationId);
        
        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Agent service is running");
    }
}
