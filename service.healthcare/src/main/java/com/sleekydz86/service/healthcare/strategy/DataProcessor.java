package com.sleekydz86.service.healthcare.strategy;

import java.util.Map;

public interface DataProcessor {
    Map<String, Object> process(Map<String, Object> data);
    boolean supports(Object value);
}

