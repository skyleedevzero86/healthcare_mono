package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.ApiResultCode;
import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.User;
import com.sleekydz86.service.auth.exception.BusinessException;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticate(LoginRequest request) {
        Map<String, Object> userMap = userMapper.findByUsername(request.getUsername());
        if (userMap == null || userMap.isEmpty()) {
            throw new BusinessException("유효하지 않은 인증 정보", ApiResultCode.AUTH_ERR);
        }

        String storedPassword = (String) userMap.get("userPw");
        if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
            throw new BusinessException("유효하지 않은 인증 정보", ApiResultCode.AUTH_ERR);
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

