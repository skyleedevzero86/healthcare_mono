package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.ApiResultCode;
import com.sleekydz86.service.auth.dto.AuthResponse;
import com.sleekydz86.service.auth.dto.LoginRequest;
import com.sleekydz86.service.auth.dto.User;
import com.sleekydz86.service.auth.exception.BusinessException;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import com.sleekydz86.service.auth.provider.JwtTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Map<String, Object> userMap;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("testUser")
                .password("password123!")
                .build();

        userMap = new HashMap<>();
        userMap.put("userSeq", 1L);
        userMap.put("userId", "testUser");
        userMap.put("userPw", "$2a$12$encodedPassword");
        userMap.put("userRole", "USER");
        userMap.put("source", "W");
        userMap.put("email", "test@example.com");
    }

    @Test
    @DisplayName("인증 성공")
    void authenticate_Success() {
        when(userMapper.findByUsername("testUser")).thenReturn(userMap);
        when(passwordEncoder.matches("password123!", "$2a$12$encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testUser", "USER", "W"))
                .thenReturn(JwtTokenDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build());

        AuthResponse response = authService.authenticate(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("accessToken");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testUser");
        verify(userMapper, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).matches("password123!", "$2a$12$encodedPassword");
        verify(jwtTokenProvider, times(1)).generateToken("testUser", "USER", "W");
    }

    @Test
    @DisplayName("인증 실패 - 사용자 없음")
    void authenticate_UserNotFound() {
        when(userMapper.findByUsername("testUser")).thenReturn(null);

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("아이디 또는 비밀번호가 올바르지 않습니다.");

        verify(userMapper, times(1)).findByUsername("testUser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("인증 실패 - 비밀번호 불일치")
    void authenticate_PasswordMismatch() {
        when(userMapper.findByUsername("testUser")).thenReturn(userMap);
        when(passwordEncoder.matches("password123!", "$2a$12$encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("아이디 또는 비밀번호가 올바르지 않습니다.");

        verify(userMapper, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).matches("password123!", "$2a$12$encodedPassword");
    }

    @Test
    @DisplayName("토큰 검증 성공")
    void validateToken_Success() throws Exception {
        when(jwtTokenProvider.validateToken("validToken")).thenReturn(true);

        boolean result = authService.validateToken("validToken");

        assertThat(result).isTrue();
        verify(jwtTokenProvider, times(1)).validateToken("validToken");
    }

    @Test
    @DisplayName("토큰 검증 실패 - 빈 토큰")
    void validateToken_EmptyToken() {
        boolean result = authService.validateToken("");

        assertThat(result).isFalse();
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("토큰 검증 실패 - null 토큰")
    void validateToken_NullToken() {
        boolean result = authService.validateToken(null);

        assertThat(result).isFalse();
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void getUserInfo_Success() {
        when(userMapper.findByUsername("testUser")).thenReturn(userMap);

        User user = authService.getUserInfo("testUser");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getRole()).isEqualTo("USER");
        verify(userMapper, times(1)).findByUsername("testUser");
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 - 사용자 없음")
    void getUserInfo_UserNotFound() {
        when(userMapper.findByUsername("testUser")).thenReturn(null);

        assertThatThrownBy(() -> authService.getUserInfo("testUser"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

        verify(userMapper, times(1)).findByUsername("testUser");
    }
}

