package com.sleekydz86.service.healthcare.service.ai;

import com.sleekydz86.service.healthcare.common.ServiceResponse;

import java.util.Map;

public interface AIResponseService {
    ServiceResponse<Map<String, Object>> getAIResponse(Map<String, Object> params);
    ServiceResponse<Integer> saveAIResponse(Map<String, Object> params);
}

