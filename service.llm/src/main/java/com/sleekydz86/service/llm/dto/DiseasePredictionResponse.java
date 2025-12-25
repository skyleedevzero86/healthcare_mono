package com.sleekydz86.service.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseasePredictionResponse {
    private String userId;
    private LocalDateTime predictionDate;
    private Integer predictionHorizonDays;
    private String overallRiskLevel;
    private Double overallRiskScore;
    private List<PredictedDisease> predictedDiseases;
    private String summary;
    private List<String> generalRecommendations;
    private Long processingTimeMs;
}

