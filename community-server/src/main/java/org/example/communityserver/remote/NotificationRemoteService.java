package org.example.communityserver.remote;

import org.example.communityserver.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "notification-server", url = "${notification.service.url:http://notification-server:8084}", path = "/notifications")
public interface NotificationRemoteService {

    @PostMapping
    ResponseEntity<Object> createNotification(@RequestBody NotificationRequest request);
}
