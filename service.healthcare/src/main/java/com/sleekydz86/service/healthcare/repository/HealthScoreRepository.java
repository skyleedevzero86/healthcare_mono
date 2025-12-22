package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.dto.ScoreDto;
import com.sleekydz86.service.healthcare.dto.TargetDto;

import java.util.Map;

public interface HealthScoreRepository {
    int getSleepScore(Map<String, Object> params);
    int saveScore(ScoreDto scoreDto);
    double getCriteriaToCalculate(String userId);
    double getWeeklyPersonalExerciseScore(Map<String, Object> map);
    int getStressScore(String userId);
    Map<String, Object> findHealthScoreInfo(String userId);
    Map<String, Object> findHealthScoreList(Map<String, Object> map);
    Map<String, Object> findTarget(TargetDto dto);
}

