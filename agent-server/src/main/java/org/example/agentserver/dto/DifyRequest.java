package org.example.agentserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyRequest {
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "查询内容不能为空")
    private String query;
    
    private String conversationId;
    
    private Map<String, Object> inputs;
    
    private String responseMode = "blocking";
    
    private String user = "user";
    
    private Map<String, Object> metadata;
}
