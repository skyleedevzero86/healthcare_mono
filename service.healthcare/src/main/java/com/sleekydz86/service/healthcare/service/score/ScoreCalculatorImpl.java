package com.sleekydz86.service.healthcare.service.score;

import com.sleekydz86.service.healthcare.repository.HealthScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScoreCalculatorImpl implements ScoreCalculator {
    private final HealthScoreRepository healthScoreRepository;

    @Override
    public int calculateSleepScore(Map<String, Object> params) {
        return healthScoreRepository.getSleepScore(params);
    }

    @Override
    public double calculateExerciseScore(Map<String, Object> params) {
        double personalScore = healthScoreRepository.getWeeklyPersonalExerciseScore(params);
        double criteriaScore = healthScoreRepository.getCriteriaToCalculate((String) params.get("userId"));
        return (personalScore / (criteriaScore * 7)) * 100;
    }

    @Override
    public int calculateStressScore(String userId) {
        return healthScoreRepository.getStressScore(userId);
    }
}

