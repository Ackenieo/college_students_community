package org.example.communityserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private Long receiverId;
    private String title;
    private String content;
    private String type = "GENERAL";
    private String priority = "NORMAL";
    private String category;
    private Map<String, Object> data;
    private String actionUrl;
    private Long expireTime;
}
