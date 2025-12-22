package com.sleekydz86.service.healthcare.service.score;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.dto.TargetDto;

import java.util.Map;

public interface HealthScoreService {
    ServiceResponse<Integer> calculateSleepScore(Map<String, Object> params);
    ServiceResponse<Integer> calculateExerciseScore(Map<String, Object> params);
    ServiceResponse<Integer> calculateStressScore(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getHealthScoreList(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getHealthScoreInfo(String userId);
    ServiceResponse<Map<String, Object>> getTarget(TargetDto dto);
}

