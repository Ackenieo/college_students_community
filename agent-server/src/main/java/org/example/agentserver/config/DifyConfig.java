package org.example.agentserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyConfig {
    
    private Api api = new Api();
    private Agent agent = new Agent();
    
    @Data
    public static class Api {
        private String baseUrl = "http://localhost/v1";
        private String apiKey;
        private int timeout = 30000;
        private int maxRetries = 3;
    }
    
    @Data
    public static class Agent {
        private String defaultModel = "gpt-3.5-turbo";
        private int maxTokens = 1000;
        private double temperature = 0.7;
    }
}
