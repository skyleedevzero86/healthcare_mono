package com.sleekydz86.web.client;

import com.sleekydz86.web.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "service.usermanagement", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/users/current")
    User getCurrentUser(@RequestHeader("Authorization") String token);
}

