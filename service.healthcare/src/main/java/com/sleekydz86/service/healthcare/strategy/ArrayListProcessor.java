package com.sleekydz86.service.healthcare.strategy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class ArrayListProcessor implements DataProcessor {
    @Override
    public Map<String, Object> process(Map<String, Object> data) {
        data.forEach((key, value) -> {
            if (value instanceof ArrayList) {
                data.put(key, value.toString());
            }
        });
        return data;
    }

    @Override
    public boolean supports(Object value) {
        return value instanceof ArrayList;
    }
}

