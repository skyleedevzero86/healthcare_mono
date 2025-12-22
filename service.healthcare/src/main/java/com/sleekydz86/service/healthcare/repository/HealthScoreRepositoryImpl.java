package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.dto.ScoreDto;
import com.sleekydz86.service.healthcare.dto.TargetDto;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

public class HealthScoreRepositoryImpl implements HealthScoreRepository {
    private final HealthcareMapper healthcareMapper;

    public HealthScoreRepositoryImpl(HealthcareMapper healthcareMapper) {
        this.healthcareMapper = healthcareMapper;
    }

    @Override
    public int getSleepScore(Map<String, Object> params) {
        return healthcareMapper.getSleepScore(params);
    }

    @Override
    public int saveScore(ScoreDto scoreDto) {
        return healthcareMapper.insScore(scoreDto);
    }

    @Override
    public double getCriteriaToCalculate(String userId) {
        return healthcareMapper.criteriaToCalculate(userId);
    }

    @Override
    public double getWeeklyPersonalExerciseScore(Map<String, Object> map) {
        return healthcareMapper.weeklyPersonalExerciseScore(map);
    }

    @Override
    public int getStressScore(String userId) {
        return healthcareMapper.StressScore(userId);
    }

    @Override
    public Map<String, Object> findHealthScoreInfo(String userId) {
        return healthcareMapper.infoHealthScore(userId);
    }

    @Override
    public Map<String, Object> findHealthScoreList(Map<String, Object> map) {
        return healthcareMapper.healthScoreList(map);
    }

    @Override
    public Map<String, Object> findTarget(TargetDto dto) {
        return healthcareMapper.getTarget(dto);
    }
}

