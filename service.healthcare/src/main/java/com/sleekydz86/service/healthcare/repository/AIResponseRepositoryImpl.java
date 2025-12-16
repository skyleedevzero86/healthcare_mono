package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

public class AIResponseRepositoryImpl implements AIResponseRepository {
    private final HealthcareMapper healthcareMapper;

    public AIResponseRepositoryImpl(HealthcareMapper healthcareMapper) {
        this.healthcareMapper = healthcareMapper;
    }

    @Override
    public Map<String, Object> findAIResponse(Map<String, Object> params) {
        return healthcareMapper.getAiResponse(params);
    }

    @Override
    public int saveAIResponse(Map<String, Object> params) {
        return healthcareMapper.insAiResponse(params);
    }
}

