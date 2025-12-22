package com.sleekydz86.service.healthcare.service.score;

import java.util.Map;

public interface ScoreCalculator {
    int calculateSleepScore(Map<String, Object> params);
    double calculateExerciseScore(Map<String, Object> params);
    int calculateStressScore(String userId);
}

