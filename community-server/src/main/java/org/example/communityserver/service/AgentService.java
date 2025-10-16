package org.example.communityserver.service;

import org.springframework.stereotype.Service;

@Service
public class AgentService {
    
    // TODO: 集成实际的Agent服务进行内容审核
    // 这里暂时返回模拟结果
    public String reviewContent(String content) {
        // 简单的关键词过滤
        if (content.contains("敏感词") || content.contains("不当内容")) {
            return "REJECTED";
        }
        return "APPROVED";
    }
}
