package com.sleekydz86.service.llm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseasePredictionRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    private String userName;
    private Integer age;
    private String gender;

    private Integer heartRate;
    private Integer bloodPressureMax;
    private Integer bloodPressureMin;
    private Double temperature;
    private Integer stress;
    private Integer oxygenSaturation;
    private Integer steps;

    private Double totalCholesterol;
    private Double ldlCholesterol;
    private Double hdlCholesterol;
    private Double triglycerides;
    private Double fastingBloodSugar;
    private Double hba1c;
    private Double bmi;

    private List<String> symptoms;
    private List<String> familyHistory;
    private List<String> currentMedications;
    private String lifestyle;
    private Integer smokingYears;
    private Integer alcoholFrequency;

    private Integer predictionHorizonDays;
    private Integer topDiseaseCount;
}
