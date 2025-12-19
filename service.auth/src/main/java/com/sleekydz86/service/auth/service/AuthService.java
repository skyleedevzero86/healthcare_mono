package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.ApiResultCode;
import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.User;
import com.sleekydz86.service.auth.exception.BusinessException;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        log.debug("사용자 인증 시작: username={}", request.getUsername());
        
        try {
            Map<String, Object> userMap = userMapper.findByUsername(request.getUsername());
            if (userMap == null || userMap.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: username={}", request.getUsername());
                throw new BusinessException(
                    "아이디 또는 비밀번호가 올바르지 않습니다.",
                    ApiResultCode.AUTH_ERR
                );
            }

            String storedPassword = (String) userMap.get("userPw");
            if (storedPassword == null || !passwordEncoder.matches(request.getPassword(), storedPassword)) {
                log.warn("비밀번호 불일치: username={}", request.getUsername());
                throw new BusinessException(
                    "아이디 또는 비밀번호가 올바르지 않습니다.",
                    ApiResultCode.AUTH_ERR
                );
            }

            String userId = (String) userMap.get("userId");
            String userRole = (String) userMap.get("userRole");
            String source = (String) userMap.get("source");
            if (source == null) {
                source = "W";
            }

            String token = jwtTokenProvider.generateToken(userId, userRole, source).getAccessToken();
            log.info("JWT 토큰 생성 완료: userId={}, role={}", userId, userRole);

            User user = User.builder()
                    .id(((Number) userMap.get("userSeq")).longValue())
                    .username(userId)
                    .email((String) userMap.get("email"))
                    .role(userRole)
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .user(user)
                    .expiresIn(3600)
                    .build();

            log.info("사용자 인증 성공: userId={}", userId);
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 인증 중 오류 발생: username={}", request.getUsername(), e);
            throw new BusinessException(
                "인증 처리 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("토큰이 비어있음");
            return false;
        }
        
        try {
            boolean isValid = jwtTokenProvider.validateToken(token);
            if (!isValid) {
                log.warn("유효하지 않은 토큰");
            }
            return isValid;
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return false;
        }
    }

    public User getUserInfo(String userId) {
        log.debug("사용자 정보 조회: userId={}", userId);
        
        try {
            Map<String, Object> userMap = userMapper.findByUsername(userId);
            if (userMap == null || userMap.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: userId={}", userId);
                throw new BusinessException(
                    "사용자를 찾을 수 없습니다.",
                    ApiResultCode.RESULT_IS_EMPTY
                );
            }

            return User.builder()
                    .id(((Number) userMap.get("userSeq")).longValue())
                    .username((String) userMap.get("userId"))
                    .email((String) userMap.get("email"))
                    .role((String) userMap.get("userRole"))
                    .build();
                    
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생: userId={}", userId, e);
            throw new BusinessException(
                "사용자 정보 조회 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        }
    }
}

