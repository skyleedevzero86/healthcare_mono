package com.sleekydz86.service.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordPolicyValidator 단위 테스트")
class PasswordPolicyValidatorTest {

    private PasswordPolicyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordPolicyValidator();
    }

    @Test
    @DisplayName("유효한 비밀번호 검증 성공")
    void validate_ValidPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("ValidPass123!");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - null")
    void validate_NullPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validate(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("비밀번호를 입력해주세요.");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 빈 문자열")
    void validate_EmptyPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("비밀번호를 입력해주세요.");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 최소 길이 미달")
    void validate_TooShort() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("Short1!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("최소 8자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 최대 길이 초과")
    void validate_TooLong() {
        String longPassword = "A".repeat(129) + "1!";
        PasswordPolicyValidator.ValidationResult result = validator.validate(longPassword);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("최대 128자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 영문자 없음")
    void validate_NoLetter() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("12345678!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("영문자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 숫자 없음")
    void validate_NoDigit() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("Password!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("숫자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 특수문자 없음")
    void validate_NoSpecialChar() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("Password123");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("특수문자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 연속된 문자")
    void validate_ConsecutiveChars() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("Password111!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("같은 문자가 3개 이상 연속");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 연속된 숫자")
    void validate_SequentialNumbers() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("Password123!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("연속된 숫자");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 약한 비밀번호")
    void validate_WeakPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validate("password123!");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("너무 간단한 비밀번호");
    }

    @Test
    @DisplayName("비밀번호 강도 계산 - 강함")
    void calculateStrength_Strong() {
        PasswordPolicyValidator.PasswordStrength strength = validator.calculateStrength("VeryStrongPassword123!@#");

        assertThat(strength).isEqualTo(PasswordPolicyValidator.PasswordStrength.STRONG);
    }

    @Test
    @DisplayName("비밀번호 강도 계산 - 보통")
    void calculateStrength_Medium() {
        PasswordPolicyValidator.PasswordStrength strength = validator.calculateStrength("MediumPass123!");

        assertThat(strength).isEqualTo(PasswordPolicyValidator.PasswordStrength.MEDIUM);
    }

    @Test
    @DisplayName("비밀번호 강도 계산 - 약함")
    void calculateStrength_Weak() {
        PasswordPolicyValidator.PasswordStrength strength = validator.calculateStrength("Weak1!");

        assertThat(strength).isEqualTo(PasswordPolicyValidator.PasswordStrength.WEAK);
    }

    @Test
    @DisplayName("비밀번호 강도 계산 - null")
    void calculateStrength_Null() {
        PasswordPolicyValidator.PasswordStrength strength = validator.calculateStrength(null);

        assertThat(strength).isEqualTo(PasswordPolicyValidator.PasswordStrength.WEAK);
    }
}

