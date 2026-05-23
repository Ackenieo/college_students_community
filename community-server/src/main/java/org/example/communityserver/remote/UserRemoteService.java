package org.example.communityserver.remote;


import org.example.common.dto.UserDTO;
import org.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-server", url = "http://user-server:8081")
public interface UserRemoteService {
    @GetMapping("/users/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") Long userId);

}
