package com.sleekydz86.service.healthcare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserIdValidator implements ConstraintValidator<ValidUserId, String> {

    private static final String USER_ID_PATTERN = "^[a-zA-Z0-9_]{1,50}$";

    @Override
    public void initialize(ValidUserId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        if (userId == null || userId.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("사용자 ID는 필수입니다")
                .addConstraintViolation();
            return false;
        }

        if (userId.length() > 50) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("사용자 ID는 50자 이하여야 합니다")
                .addConstraintViolation();
            return false;
        }

        if (!userId.matches(USER_ID_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("사용자 ID는 영문, 숫자, 언더스코어만 허용됩니다")
                .addConstraintViolation();
            return false;
        }

        String lowerUserId = userId.toLowerCase();
        String[] dangerousPatterns = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "exec", "execute", "script", "<script", "javascript:", "onerror",
            "onload", "onclick", "eval(", "expression("
        };

        for (String pattern : dangerousPatterns) {
            if (lowerUserId.contains(pattern)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("사용자 ID에 허용되지 않는 문자가 포함되어 있습니다")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}

