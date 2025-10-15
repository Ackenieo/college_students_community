package org.example.agentserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    
    private String id;
    private String answer;
    private String conversationId;
    private String status;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private String error;
    
    public static AgentResponse success(String answer, String conversationId) {
        AgentResponse response = new AgentResponse();
        response.setAnswer(answer);
        response.setConversationId(conversationId);
        response.setStatus("success");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    public static AgentResponse error(String error) {
        AgentResponse response = new AgentResponse();
        response.setError(error);
        response.setStatus("error");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
