package com.sleekydz86.service.usermanagement.service.user;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.repository.UserJpaRepository;
import com.sleekydz86.service.usermanagement.repository.UserRepository;
import com.sleekydz86.service.usermanagement.service.cache.CacheService;
import com.sleekydz86.service.usermanagement.util.DtoConverter;
import com.sleekydz86.service.usermanagement.util.PasswordPolicyValidator;
import com.sleekydz86.service.usermanagement.global.util.HealthcareEncryptionUtil;
import com.sleekydz86.service.usermanagement.metrics.UserManagementMetrics;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserManagementService 단위 테스트")
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordPolicyValidator passwordPolicyValidator;

    @Mock
    private UserManagementMetrics userManagementMetrics;

    @Mock
    private DtoConverter dtoConverter;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .userId("testUser")
                .userPwEnc("currentPassword")
                .newUserPwEnc("NewPassword123!")
                .userNm("테스트 사용자")
                .email("test@example.com")
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_Success() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userPwEnc", HealthcareEncryptionUtil.hashPassword("currentPassword", "salt"));
        userInfo.put("userSalt", "salt");

        when(userRepository.findUserInfo(any(UserDto.class))).thenReturn(userInfo);
        when(passwordPolicyValidator.validate("NewPassword123!"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());
        when(userRepository.updatePassword(any(UserDto.class))).thenReturn(1);

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(1);
        verify(userRepository, times(1)).findUserInfo(any(UserDto.class));
        verify(passwordPolicyValidator, times(1)).validate("NewPassword123!");
        verify(userRepository, times(1)).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 없음")
    void updatePassword_NoCurrentPassword() {
        userDto.setUserPwEnc(null);

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("현재 비밀번호를 입력해주세요.");
        verify(userRepository, never()).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 새 비밀번호 없음")
    void updatePassword_NoNewPassword() {
        userDto.setNewUserPwEnc(null);

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("새 비밀번호를 입력해주세요.");
        verify(userRepository, never()).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 정책 위반")
    void updatePassword_PolicyViolation() {
        userDto.setNewUserPwEnc("short");
        when(passwordPolicyValidator.validate("short"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.failure("비밀번호는 최소 8자 이상이어야 합니다."));

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("비밀번호는 최소 8자 이상이어야 합니다.");
        verify(passwordPolicyValidator, times(1)).validate("short");
        verify(userRepository, never()).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호와 동일")
    void updatePassword_SamePassword() {
        userDto.setNewUserPwEnc("currentPassword");
        when(passwordPolicyValidator.validate("currentPassword"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        verify(userRepository, never()).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 사용자 없음")
    void updatePassword_UserNotFound() {
        when(userRepository.findUserInfo(any(UserDto.class))).thenReturn(null);
        when(passwordPolicyValidator.validate("NewPassword123!"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());

        ServiceResponse<Integer> response = userManagementService.updatePassword(userDto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("사용자를 찾을 수 없습니다.");
        verify(userRepository, times(1)).findUserInfo(any(UserDto.class));
        verify(userRepository, never()).updatePassword(any(UserDto.class));
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() {
        user.setPassword("ValidPass123!");
        when(passwordPolicyValidator.validate("ValidPass123!"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());
        when(passwordEncoder.encode("ValidPass123!")).thenReturn("encodedPassword");
        when(userJpaRepository.save(any(User.class))).thenReturn(user);

        User result = userManagementService.createUser(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(passwordPolicyValidator, times(1)).validate("ValidPass123!");
        verify(passwordEncoder, times(1)).encode("ValidPass123!");
        verify(userJpaRepository, times(1)).save(any(User.class));
        verify(cacheService, times(1)).cacheUserData(user);
    }

    @Test
    @DisplayName("사용자 생성 실패 - 비밀번호 정책 위반")
    void createUser_PasswordPolicyViolation() {
        user.setPassword("short");
        when(passwordPolicyValidator.validate("short"))
                .thenReturn(PasswordPolicyValidator.ValidationResult.failure("비밀번호는 최소 8자 이상이어야 합니다."));

        try {
            userManagementService.createUser(user);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        verify(passwordPolicyValidator, times(1)).validate("short");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void getUser_Success() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userManagementService.getUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userJpaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("사용자 조회 실패 - 없음")
    void getUser_NotFound() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userManagementService.getUser(1L);

        assertThat(result).isNull();
        verify(userJpaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");

        when(userJpaRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userManagementService.updateUser(user);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("updatedUser");
        verify(userJpaRepository, times(1)).save(user);
        verify(cacheService, times(1)).cacheUserData(updatedUser);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() {
        doNothing().when(userJpaRepository).deleteById(1L);

        userManagementService.deleteUser(1L);

        verify(userJpaRepository, times(1)).deleteById(1L);
    }
}


