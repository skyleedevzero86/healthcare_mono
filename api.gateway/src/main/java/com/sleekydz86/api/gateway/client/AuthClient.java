package com.sleekydz86.api.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service.auth", fallback = AuthClientFallback.class)
public interface AuthClient {

    @PostMapping("/api/auth/validate")
    Boolean validateToken(@RequestBody TokenRequest request);
}

