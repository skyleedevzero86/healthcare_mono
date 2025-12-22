package com.sleekydz86.service.healthcare.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StringProcessor implements DataProcessor {
    @Override
    public Map<String, Object> process(Map<String, Object> data) {
        data.forEach((key, value) -> {
            if (value instanceof String) {
                data.put(key, ((String) value).trim());
            }
        });
        return data;
    }

    @Override
    public boolean supports(Object value) {
        return value instanceof String;
    }
}

