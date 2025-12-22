package com.sleekydz86.service.usermanagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class PasswordPolicyValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[a-zA-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    private static final Pattern CONSECUTIVE_PATTERN = Pattern.compile(".*(.)\\1{2,}.*");
    private static final Pattern SEQUENTIAL_PATTERN = Pattern.compile(".*(012|123|234|345|456|567|678|789|890|987|876|765|654|543|432|321|210).*");

    public ValidationResult validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            return ValidationResult.failure("비밀번호를 입력해주세요.");
        }

        if (password.length() < MIN_LENGTH) {
            return ValidationResult.failure(
                String.format("비밀번호는 최소 %d자 이상이어야 합니다.", MIN_LENGTH)
            );
        }

        if (password.length() > MAX_LENGTH) {
            return ValidationResult.failure(
                String.format("비밀번호는 최대 %d자까지 가능합니다.", MAX_LENGTH)
            );
        }

        if (!LETTER_PATTERN.matcher(password).matches()) {
            return ValidationResult.failure("비밀번호에 영문자가 포함되어야 합니다.");
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            return ValidationResult.failure("비밀번호에 숫자가 포함되어야 합니다.");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            return ValidationResult.failure("비밀번호에 특수문자가 포함되어야 합니다.");
        }

        if (CONSECUTIVE_PATTERN.matcher(password).matches()) {
            return ValidationResult.failure("비밀번호에 같은 문자가 3개 이상 연속으로 사용될 수 없습니다.");
        }

        if (SEQUENTIAL_PATTERN.matcher(password).matches()) {
            return ValidationResult.failure("비밀번호에 연속된 숫자가 포함될 수 없습니다.");
        }

        if (isCommonWeakPassword(password)) {
            return ValidationResult.failure("너무 간단한 비밀번호는 사용할 수 없습니다.");
        }

        return ValidationResult.success();
    }

    private boolean isCommonWeakPassword(String password) {
        String lowerPassword = password.toLowerCase();
        String[] weakPasswords = {
            "password", "12345678", "qwerty", "abc123", "password123",
            "admin", "letmein", "welcome", "monkey", "1234567890"
        };
        for (String weak : weakPasswords) {
            if (lowerPassword.contains(weak)) {
                return true;
            }
        }
        return false;
    }

    public PasswordStrength calculateStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        if (password.length() >= 12) {
            score += 2;
        } else if (password.length() >= 10) {
            score += 1;
        }

        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            score += 1;
        }

        if (DIGIT_PATTERN.matcher(password).matches()) {
            score += 1;
        }

        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            score += 1;
        }

        if (password.length() >= 16) {
            score += 1;
        }

        if (score >= 5) {
            return PasswordStrength.STRONG;
        } else if (score >= 3) {
            return PasswordStrength.MEDIUM;
        } else {
            return PasswordStrength.WEAK;
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "비밀번호가 정책을 만족합니다.");
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum PasswordStrength {
        WEAK("약함"),
        MEDIUM("보통"),
        STRONG("강함");

        private final String description;

        PasswordStrength(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}


