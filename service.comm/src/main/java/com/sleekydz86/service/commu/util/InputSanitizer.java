package com.sleekydz86.service.commu.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final int MAX_CONTENT_LENGTH = 50000;
    private static final int MAX_TITLE_LENGTH = 500;

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union\\s+select|insert\\s+into|update\\s+set|delete\\s+from|drop\\s+table|create\\s+table|alter\\s+table|exec\\s*\\(|execute\\s*\\(|script|javascript:|onerror\\s*=|onload\\s*=|eval\\s*\\(|expression\\s*\\(|--|/\\*|\\*/|xp_cmdshell|sp_executesql)"
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script[^>]*>|</script>|javascript:|onerror\\s*=|onload\\s*=|onclick\\s*=|onmouseover\\s*=|eval\\s*\\(|expression\\s*\\(|vbscript:|data:text/html|base64)"
    );

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        if (input.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("입력값이 너무 깁니다. 최대 " + MAX_CONTENT_LENGTH + "자까지 허용됩니다.");
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

    public String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        if (input.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("입력값이 너무 깁니다. 최대 " + MAX_CONTENT_LENGTH + "자까지 허용됩니다.");
        }
        
        String sanitized = input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
        
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
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
        if (userId.length() > 50) {
            throw new IllegalArgumentException("사용자 ID가 너무 깁니다.");
        }
        return userId.replaceAll("[^a-zA-Z0-9_]", "");
    }
}

