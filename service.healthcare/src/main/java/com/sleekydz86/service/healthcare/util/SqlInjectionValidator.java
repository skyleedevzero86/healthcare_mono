package com.sleekydz86.service.healthcare.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SqlInjectionValidator {

    private static final Set<String> ALLOWED_COLUMN_NAMES = new HashSet<>(Arrays.asList(
        "heartrate", "temperature", "spo2", "stress", "bloodpress", "repiratory", "step", "sleep"
    ));

    private static final Pattern COLUMN_NAME_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$");

    public static boolean isValidColumnName(String columnName) {
        if (columnName == null || columnName.trim().isEmpty()) {
            return false;
        }

        String normalized = columnName.toLowerCase().trim();

        if (!COLUMN_NAME_PATTERN.matcher(normalized).matches()) {
            log.warn("잘못된 컬럼명 형식: {}", columnName);
            return false;
        }

        if (!ALLOWED_COLUMN_NAMES.contains(normalized)) {
            log.warn("허용되지 않은 컬럼명: {}", columnName);
            return false;
        }

        return true;
    }

    public static boolean isValidCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            return false;
        }

        if (!NUMBER_PATTERN.matcher(condition.trim()).matches()) {
            log.warn("잘못된 조건값 형식: {}", condition);
            return false;
        }

        try {
            int value = Integer.parseInt(condition.trim());
            if (value <= 0 || value > 60) {
                log.warn("허용 범위를 벗어난 조건값: {}", condition);
                return false;
            }
        } catch (NumberFormatException e) {
            log.warn("숫자로 변환할 수 없는 조건값: {}", condition);
            return false;
        }

        return true;
    }

    public static String sanitizeColumnName(String columnName) {
        if (!isValidColumnName(columnName)) {
            throw new IllegalArgumentException("유효하지 않은 컬럼명입니다: " + columnName);
        }
        return columnName.toLowerCase().trim();
    }

    public static String sanitizeCondition(String condition) {
        if (!isValidCondition(condition)) {
            throw new IllegalArgumentException("유효하지 않은 조건값입니다: " + condition);
        }
        return condition.trim();
    }
}


