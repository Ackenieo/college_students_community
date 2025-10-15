package org.example.agentserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyResponse {
    
    private String id;
    private String message;
    private String conversationId;
    private String mode;
    private String answer;
    private Map<String, Object> metadata;
    private String created_at;
    private List<Map<String, Object>> suggestions;
    private String taskId;
    private String status;
    
    // 错误信息
    private String error;
    private String code;
}
