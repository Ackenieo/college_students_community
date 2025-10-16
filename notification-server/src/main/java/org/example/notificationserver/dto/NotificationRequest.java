package org.example.notificationserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 200, message = "通知标题长度不能超过200个字符")
    private String title;
    
    @NotBlank(message = "通知内容不能为空")
    @Size(max = 1000, message = "通知内容长度不能超过1000个字符")
    private String content;
    
    private String type = "GENERAL"; // 通知类型：GENERAL, FRIEND_REQUEST, MESSAGE, LIKE, COMMENT
    
    private String priority = "NORMAL"; // 优先级：HIGH, NORMAL, LOW
    
    private String category; // 通知分类
    
    private Map<String, Object> data; // 额外数据
    
    private String actionUrl; // 点击通知后的跳转链接
    
    private Long expireTime; // 过期时间戳
}
