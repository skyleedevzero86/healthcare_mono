package com.sleekydz86.service.llm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class InputSanitizer {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|onerror|onload)");

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)(<script|</script>|javascript:|onerror=|onload=|eval\\(|expression\\()");

    public boolean isSafe(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            log.warn("SQL Injection 패턴 감지: {}", input);
            return false;
        }

        if (XSS_PATTERN.matcher(input).find()) {
            log.warn("XSS 패턴 감지: {}", input);
            return false;
        }

        return true;
    }

    public String sanitize(String input) {
        if (input == null) {
            return "";
        }

        String sanitized = input.replaceAll("<[^>]*>", "");

        sanitized = sanitized.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");

        return sanitized.trim();
    }

    public boolean isValidLength(String prompt, int maxLength) {
        if (prompt == null) {
            return false;
        }
        return prompt.length() <= maxLength;
    }
}
