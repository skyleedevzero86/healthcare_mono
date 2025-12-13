package com.sleekydz86.service.healthcare.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|onerror|onload)"
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script>|javascript:|onerror=|onload=|eval\\(|expression\\()"
    );

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String sanitized = input.trim();
        sanitized = SQL_INJECTION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = XSS_PATTERN.matcher(sanitized).replaceAll("");
        return sanitized;
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
        return userId.replaceAll("[^a-zA-Z0-9_]", "");
    }
}

