package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.util.PasswordPolicyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordService 단위 테스트")
class PasswordServiceTest {

    @Mock
    private PasswordPolicyValidator passwordPolicyValidator;

    @InjectMocks
    private PasswordService passwordService;

    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder(12);
    }

    @Test
    @DisplayName("비밀번호 인코딩 성공")
    void encode_Success() {
        String rawPassword = "ValidPass123!";
        when(passwordPolicyValidator.validate(rawPassword))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());

        String encoded = passwordService.encode(rawPassword);

        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
        verify(passwordPolicyValidator, times(1)).validate(rawPassword);
    }

    @Test
    @DisplayName("비밀번호 인코딩 실패 - 빈 비밀번호")
    void encode_EmptyPassword() {
        assertThatThrownBy(() -> passwordService.encode(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호를 입력해주세요.");

        verify(passwordPolicyValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("비밀번호 인코딩 실패 - null 비밀번호")
    void encode_NullPassword() {
        assertThatThrownBy(() -> passwordService.encode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호를 입력해주세요.");

        verify(passwordPolicyValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("비밀번호 인코딩 실패 - 정책 위반")
    void encode_PolicyViolation() {
        String invalidPassword = "short";
        when(passwordPolicyValidator.validate(invalidPassword))
                .thenReturn(PasswordPolicyValidator.ValidationResult.failure("비밀번호는 최소 8자 이상이어야 합니다."));

        assertThatThrownBy(() -> passwordService.encode(invalidPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.");

        verify(passwordPolicyValidator, times(1)).validate(invalidPassword);
    }

    @Test
    @DisplayName("비밀번호 매칭 성공")
    void matches_Success() {
        String rawPassword = "ValidPass123!";
        String encodedPassword = encoder.encode(rawPassword);

        boolean result = passwordService.matches(rawPassword, encodedPassword);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 매칭 실패")
    void matches_Failure() {
        String rawPassword = "ValidPass123!";
        String wrongPassword = "WrongPass123!";
        String encodedPassword = encoder.encode(rawPassword);

        boolean result = passwordService.matches(wrongPassword, encodedPassword);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("비밀번호 매칭 실패 - null 비밀번호")
    void matches_NullPassword() {
        boolean result = passwordService.matches(null, "encoded");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("비밀번호 매칭 실패 - null 인코딩된 비밀번호")
    void matches_NullEncodedPassword() {
        boolean result = passwordService.matches("password", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("비밀번호 정책 검증 성공")
    void validatePasswordPolicy_Success() {
        String validPassword = "ValidPass123!";
        when(passwordPolicyValidator.validate(validPassword))
                .thenReturn(PasswordPolicyValidator.ValidationResult.success());

        passwordService.validatePasswordPolicy(validPassword);

        verify(passwordPolicyValidator, times(1)).validate(validPassword);
    }

    @Test
    @DisplayName("비밀번호 정책 검증 실패")
    void validatePasswordPolicy_Failure() {
        String invalidPassword = "short";
        when(passwordPolicyValidator.validate(invalidPassword))
                .thenReturn(PasswordPolicyValidator.ValidationResult.failure("비밀번호는 최소 8자 이상이어야 합니다."));

        assertThatThrownBy(() -> passwordService.validatePasswordPolicy(invalidPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.");

        verify(passwordPolicyValidator, times(1)).validate(invalidPassword);
    }
}


