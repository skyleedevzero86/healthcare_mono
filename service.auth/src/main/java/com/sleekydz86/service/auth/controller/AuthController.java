package com.sleekydz86.service.auth.controller;

import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.TokenRequest;
import com.sleekydz86.service.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);

            String sessionKey = "session:" + response.getToken();
            redisTemplate.opsForValue().set(sessionKey, response.getUser(), Duration.ofHours(24));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody TokenRequest request) {
        boolean isValid = authService.validateToken(request.getToken());
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String sessionKey = "session:" + token.replace("Bearer ", "");
        redisTemplate.delete(sessionKey);

        return ResponseEntity.ok().build();
    }
}

