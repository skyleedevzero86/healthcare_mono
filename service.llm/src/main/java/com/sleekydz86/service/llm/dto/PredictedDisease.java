package com.sleekydz86.service.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictedDisease {
    private String diseaseName;
    private String diseaseCode;
    private Double probability;
    private String riskLevel;
    private String description;
    private List<String> riskFactors;
    private List<String> preventiveMeasures;
    private Integer estimatedOnsetDays;
    private String severity;
}

