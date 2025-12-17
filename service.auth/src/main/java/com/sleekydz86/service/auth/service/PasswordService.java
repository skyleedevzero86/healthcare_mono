package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.util.PasswordPolicyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final PasswordPolicyValidator passwordPolicyValidator;

    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        
        PasswordPolicyValidator.ValidationResult result = passwordPolicyValidator.validate(rawPassword);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getMessage());
        }
        
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        if (rawPassword.length() > 128) {
            return false;
        }
        
        return encoder.matches(rawPassword, encodedPassword);
    }

    public void validatePasswordPolicy(String password) {
        PasswordPolicyValidator.ValidationResult result = passwordPolicyValidator.validate(password);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getMessage());
        }
    }
}

