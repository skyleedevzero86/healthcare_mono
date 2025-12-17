package com.sleekydz86.service.auth.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final int MAX_INPUT_LENGTH = 10000;
    private static final int MAX_USER_ID_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 255;

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union\\s+select|insert\\s+into|update\\s+set|delete\\s+from|drop\\s+table|create\\s+table|alter\\s+table|exec\\s*\\(|execute\\s*\\(|script|javascript:|onerror\\s*=|onload\\s*=|eval\\s*\\(|expression\\s*\\(|--|/\\*|\\*/|xp_cmdshell|sp_executesql)"
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script[^>]*>|</script>|javascript:|onerror\\s*=|onload\\s*=|onclick\\s*=|onmouseover\\s*=|eval\\s*\\(|expression\\s*\\(|vbscript:|data:text/html|base64)"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USER_ID_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,50}$"
    );

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        if (input.length() > MAX_INPUT_LENGTH) {
            throw new IllegalArgumentException("입력값이 너무 깁니다. 최대 " + MAX_INPUT_LENGTH + "자까지 허용됩니다.");
        }
        
        String sanitized = input.trim();
        
        if (containsSqlInjection(sanitized)) {
            throw new IllegalArgumentException("잘못된 입력값이 포함되어 있습니다.");
        }
        
        if (containsXss(sanitized)) {
            throw new IllegalArgumentException("잘못된 입력값이 포함되어 있습니다.");
        }
        
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        return sanitized;
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public boolean containsXss(String input) {
        if (input == null) {
            return false;
        }
        return XSS_PATTERN.matcher(input).find();
    }

    public String sanitizeUserId(String userId) {
        if (userId == null) {
            return null;
        }
        
        if (userId.length() > MAX_USER_ID_LENGTH) {
            throw new IllegalArgumentException("사용자 ID가 너무 깁니다. 최대 " + MAX_USER_ID_LENGTH + "자까지 허용됩니다.");
        }
        
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            throw new IllegalArgumentException("사용자 ID는 영문, 숫자, 언더스코어만 사용할 수 있으며 3자 이상 50자 이하여야 합니다.");
        }
        
        return userId;
    }

    public boolean isValidUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        if (userId.length() > MAX_USER_ID_LENGTH || userId.length() < 3) {
            return false;
        }
        return USER_ID_PATTERN.matcher(userId).matches();
    }
}

