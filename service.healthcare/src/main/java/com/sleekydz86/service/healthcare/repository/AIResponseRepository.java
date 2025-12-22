package com.sleekydz86.service.healthcare.repository;

import java.util.Map;

public interface AIResponseRepository {
    Map<String, Object> findAIResponse(Map<String, Object> params);
    int saveAIResponse(Map<String, Object> params);
}

