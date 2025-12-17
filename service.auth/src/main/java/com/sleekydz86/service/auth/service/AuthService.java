package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.User;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(LoginRequest request) {
        Map<String, Object> userMap = userMapper.findByUsername(request.getUsername());
        if (userMap == null || userMap.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        String storedPassword = (String) userMap.get("userPw");
        if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
            throw new RuntimeException("Invalid credentials");
        }

        String userId = (String) userMap.get("userId");
        String userRole = (String) userMap.get("userRole");
        String source = (String) userMap.get("source");
        if (source == null) {
            source = "W";
        }

        String token = jwtTokenProvider.generateToken(userId, userRole, source).getAccessToken();

        User user = new User();
        user.setId(((Number) userMap.get("userSeq")).longValue());
        user.setUsername(userId);
        user.setEmail((String) userMap.get("email"));
        user.setRole(userRole);

        return AuthResponse.builder()
            .token(token)
            .user(user)
            .expiresIn(3600)
            .build();
    }

    public boolean validateToken(String token) {
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
}

