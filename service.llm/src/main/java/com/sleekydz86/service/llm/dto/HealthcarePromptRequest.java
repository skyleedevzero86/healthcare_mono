package com.sleekydz86.service.llm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthcarePromptRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;
    
    private String userName;
    
    private Integer age;
    
    private Integer heartRate;
    private Integer bloodPressureMax;
    private Integer bloodPressureMin;
    private Double temperature;
    private Integer stress;
    private Integer oxygenSaturation;
    private Integer steps;
    
    private Double totalCholesterol;
    private Double fastingBloodSugar;
    private Double hba1c;
    private Double bmi;
    
    private String userQuestion;
    
    private Boolean includeDiseaseRecommendation;
    private Boolean includeFoodRecommendation;
    private Boolean includeExerciseRecommendation;
    private Integer recommendationCount;
}

