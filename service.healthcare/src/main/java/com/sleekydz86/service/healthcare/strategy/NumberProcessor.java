package com.sleekydz86.service.healthcare.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NumberProcessor implements DataProcessor {
    @Override
    public Map<String, Object> process(Map<String, Object> data) {
        data.forEach((key, value) -> {
            if (value instanceof Number) {
                if (value instanceof Double && ((Double) value).isNaN()) {
                    data.put(key, 0);
                } else if (value instanceof Double && ((Double) value).isInfinite()) {
                    data.put(key, 0);
                } else if (value instanceof Float && ((Float) value).isNaN()) {
                    data.put(key, 0);
                } else if (value instanceof Float && ((Float) value).isInfinite()) {
                    data.put(key, 0);
                }
            }
        });
        return data;
    }

    @Override
    public boolean supports(Object value) {
        return value instanceof Number;
    }
}

