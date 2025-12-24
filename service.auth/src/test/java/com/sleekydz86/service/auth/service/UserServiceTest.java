package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.*;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import com.sleekydz86.service.auth.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserServiceImpl userService;

    private SigninDto signinDto;
    private SignupDto signupDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        String uniqueId = "testUser_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        signinDto = SigninDto.builder()
                .userId(uniqueId)
                .userPwEnc("encodedPassword")
                .userRoleFk("1")
                .source("web")
                .build();

        signupDto = SignupDto.builder()
                .userId(uniqueId)
                .userPwEnc("encodedPassword")
                .userNm("테스트 사용자")
                .userRoleFk("1")
                .birthEnc("19900101")
                .telNumEnc("01012345678")
                .email("test_" + System.currentTimeMillis() + "@example.com")
                .deptNm("IT")
                .height(170.0f)
                .weight(70.0f)
                .gender("M")
                .bloodType("A")
                .build();

        userDto = new UserDto();
        userDto.setUserId(uniqueId);
        userDto.setUserPwEnc("encodedPassword");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        signinDto = null;
        signupDto = null;
        userDto = null;
        reset(userMapper, jwtTokenProvider, passwordService);
    }

    @Test
    @DisplayName("로그인 성공")
    void signin_Success() throws Exception {
        UserDto mockUser = new UserDto();
        mockUser.setUserId("testUser");
        mockUser.setUserPwEnc("encodedPassword");
        mockUser.setUserRoleFk("1");

        when(userMapper.signin(any(SigninDto.class))).thenReturn(mockUser);
        when(passwordService.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString()))
                .thenReturn(JwtTokenDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .grantType("Bearer")
                        .build());

        Map<Object, Object> result = userService.signin(signinDto);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("accessToken");
        verify(userMapper, times(1)).signin(signinDto);
        verify(passwordService, times(1)).matches(anyString(), anyString());
        verify(jwtTokenProvider, times(1)).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() throws Exception {
        when(userMapper.duplicateId(any(UserDto.class))).thenReturn(null);
        when(userMapper.duplicateEmail(any(UserDto.class))).thenReturn(null);
        when(passwordService.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.signup(any(SignupDto.class))).thenReturn(1);
        when(userMapper.getUserSeq(anyString())).thenReturn(1);
        when(userMapper.insUserAuth(any(SignupDto.class))).thenReturn(1);

        int result = userService.signup(signupDto);

        assertThat(result).isEqualTo(1);
        verify(userMapper, times(1)).duplicateId(any(UserDto.class));
        verify(userMapper, times(1)).duplicateEmail(any(UserDto.class));
        verify(passwordService, times(1)).encode(anyString());
        verify(userMapper, times(1)).signup(any(SignupDto.class));
        verify(userMapper, times(1)).insUserAuth(any(SignupDto.class));
    }

    @Test
    @DisplayName("아이디 중복 확인 - 중복됨")
    void duplicateId_Exists() {
        when(userMapper.duplicateId(any(UserDto.class))).thenReturn(1);

        boolean result = userService.duplicateId(userDto);

        assertThat(result).isTrue();
        verify(userMapper, times(1)).duplicateId(userDto);
    }

    @Test
    @DisplayName("아이디 중복 확인 - 중복 안됨")
    void duplicateId_NotExists() {
        when(userMapper.duplicateId(any(UserDto.class))).thenReturn(null);

        boolean result = userService.duplicateId(userDto);

        assertThat(result).isFalse();
        verify(userMapper, times(1)).duplicateId(userDto);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복됨")
    void duplicateEmail_Exists() {
        when(userMapper.duplicateEmail(any(UserDto.class))).thenReturn(1);

        boolean result = userService.duplicateEmail(userDto);

        assertThat(result).isTrue();
        verify(userMapper, times(1)).duplicateEmail(userDto);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복 안됨")
    void duplicateEmail_NotExists() {
        when(userMapper.duplicateEmail(any(UserDto.class))).thenReturn(null);

        boolean result = userService.duplicateEmail(userDto);

        assertThat(result).isFalse();
        verify(userMapper, times(1)).duplicateEmail(userDto);
    }

    @Test
    @DisplayName("의사 검색 성공")
    void searchDoctor_Success() {
        List<Map<String, Object>> expectedList = List.of(new HashMap<>());
        when(userMapper.searchDoctor(any(UserDto.class))).thenReturn(expectedList);

        List<Map<String, Object>> result = userService.searchDoctor(userDto);

        assertThat(result).isNotNull();
        verify(userMapper, times(1)).searchDoctor(userDto);
    }

    @Test
    @DisplayName("보호자 검색 성공")
    void searchParent_Success() {
        List<Map<String, Object>> expectedList = List.of(new HashMap<>());
        when(userMapper.searchParent(any(UserDto.class))).thenReturn(expectedList);

        List<Map<String, Object>> result = userService.searchParent(userDto);

        assertThat(result).isNotNull();
        verify(userMapper, times(1)).searchParent(userDto);
    }

    @Test
    @DisplayName("userSeq 조회 성공")
    void getUserSeq_Success() {
        when(userMapper.getUserSeq(anyString())).thenReturn(1);

        Integer result = userService.getUserSeq("testUser");

        assertThat(result).isEqualTo(1);
        verify(userMapper, times(1)).getUserSeq("testUser");
    }

    @Test
    @DisplayName("아이디 찾기 성공")
    void findUserId_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("userId", "testUser");
        when(userMapper.findUserId(any(FindDto.class))).thenReturn(expectedData);

        Map<String, Object> result = userService.findUserId(new FindDto());

        assertThat(result).isNotNull();
        assertThat(result).containsKey("userId");
        verify(userMapper, times(1)).findUserId(any(FindDto.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 성공")
    void findUserPw_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("userId", "testUser");
        when(userMapper.findUserPw(any(FindDto.class))).thenReturn(expectedData);

        Map<String, Object> result = userService.findUserPw(new FindDto());

        assertThat(result).isNotNull();
        assertThat(result).containsKey("userId");
        verify(userMapper, times(1)).findUserPw(any(FindDto.class));
    }
}

