package com.sleekydz86.service.auth.controller;

import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.TokenRequest;
import com.sleekydz86.service.auth.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    public AuthController(AuthService authService, org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate) {
        this.authService = authService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);

        String sessionKey = "session:" + response.getToken();
        redisTemplate.opsForValue().set(sessionKey, response.getUser(), Duration.ofHours(24));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody TokenRequest request) {
        boolean isValid = authService.validateToken(request.getToken());
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String userId = com.sleekydz86.service.auth.util.UserContext.getUserId();
        if (userId != null && !userId.isEmpty()) {
            String sessionKey = "session:" + userId;
            redisTemplate.delete(sessionKey);
        }
        return ResponseEntity.ok().build();
    }
}

