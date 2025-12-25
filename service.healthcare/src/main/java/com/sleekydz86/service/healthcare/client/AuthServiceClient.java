package com.sleekydz86.service.healthcare.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
    name = "service.auth",
    path = "/auth/v1",
    fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {
    
    @PostMapping("/validateToken")
    Map<String, Object> validateToken(@RequestBody Map<String, Object> request);
    
    @PostMapping("/getUserInfo")
    Map<String, Object> getUserInfo(@RequestBody Map<String, Object> request);

    @PostMapping("/getUserSeq")
    Map<String, Object> getUserSeq(@RequestBody Map<String, String> request);
}

