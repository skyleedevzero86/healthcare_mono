package com.sleekydz86.service.llm.infrastructure.adapter.tool;

import com.sleekydz86.service.llm.application.DiseasePredictionService;
import com.sleekydz86.service.llm.domain.tool.Tool;
import com.sleekydz86.service.llm.dto.DiseasePredictionRequest;
import com.sleekydz86.service.llm.dto.DiseasePredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiseasePredictionTool implements Tool {
    
    private final DiseasePredictionService diseasePredictionService;
    
    @Override
    public String getName() {
        return "disease_prediction";
    }
    
    @Override
    public String getDescription() {
        return "건강 데이터를 분석하여 질병 발생 가능성을 예측합니다. 심박수, 혈압, 혈당, 콜레스테롤 등의 데이터를 기반으로 위험도를 평가합니다.";
    }
    
    @Override
    public ToolSchema getSchema() {
        Map<String, ParameterSchema> parameters = new HashMap<>();
        parameters.put("userId", ParameterSchema.builder()
                .type("string")
                .description("사용자 ID")
                .required(true)
                .build());
        parameters.put("heartRate", ParameterSchema.builder()
                .type("integer")
                .description("심박수 (bpm)")
                .required(false)
                .build());
        parameters.put("bloodPressureMax", ParameterSchema.builder()
                .type("integer")
                .description("수축기 혈압")
                .required(false)
                .build());
        parameters.put("bloodPressureMin", ParameterSchema.builder()
                .type("integer")
                .description("이완기 혈압")
                .required(false)
                .build());
        parameters.put("temperature", ParameterSchema.builder()
                .type("number")
                .description("체온 (°C)")
                .required(false)
                .build());
        parameters.put("bmi", ParameterSchema.builder()
                .type("number")
                .description("BMI")
                .required(false)
                .build());
        parameters.put("totalCholesterol", ParameterSchema.builder()
                .type("number")
                .description("총 콜레스테롤 (mg/dL)")
                .required(false)
                .build());
        parameters.put("fastingBloodSugar", ParameterSchema.builder()
                .type("number")
                .description("공복혈당 (mg/dL)")
                .required(false)
                .build());
        parameters.put("hba1c", ParameterSchema.builder()
                .type("number")
                .description("당화혈색소 (%)")
                .required(false)
                .build());
        parameters.put("predictionHorizonDays", ParameterSchema.builder()
                .type("integer")
                .description("예측 기간 (일)")
                .required(false)
                .defaultValue(90)
                .build());
        parameters.put("topDiseaseCount", ParameterSchema.builder()
                .type("integer")
                .description("상위 질병 개수")
                .required(false)
                .defaultValue(5)
                .build());
        
        return ToolSchema.builder()
                .name(getName())
                .description(getDescription())
                .parameters(parameters)
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> input) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null || input.isEmpty()) {
                return ToolResult.failure("입력 데이터가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            String userId = (String) input.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ToolResult.failure("사용자 ID가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            DiseasePredictionRequest request = buildRequest(input);
            DiseasePredictionResponse response = diseasePredictionService.predict(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("overallRiskLevel", response.getOverallRiskLevel());
            result.put("overallRiskScore", response.getOverallRiskScore());
            result.put("summary", response.getSummary());
            result.put("predictedDiseases", response.getPredictedDiseases());
            result.put("generalRecommendations", response.getGeneralRecommendations());
            result.put("processingTimeMs", response.getProcessingTimeMs());
            
            return ToolResult.success(result, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("질병 예측 도구 실행 오류", e);
            return ToolResult.failure("질병 예측 중 오류가 발생했습니다: " + e.getMessage(), 
                    System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public boolean canHandle(String action) {
        return action != null && (
            action.toLowerCase().contains("disease") ||
            action.toLowerCase().contains("질병") ||
            action.toLowerCase().contains("예측") ||
            action.toLowerCase().contains("prediction") ||
            action.toLowerCase().contains("risk") ||
            action.toLowerCase().contains("위험")
        );
    }
    
    private DiseasePredictionRequest buildRequest(Map<String, Object> input) {
        DiseasePredictionRequest.DiseasePredictionRequestBuilder builder = DiseasePredictionRequest.builder();
        
        if (input.get("userId") != null) {
            builder.userId((String) input.get("userId"));
        }
        if (input.get("userName") != null) {
            builder.userName((String) input.get("userName"));
        }
        if (input.get("age") != null) {
            builder.age(((Number) input.get("age")).intValue());
        }
        if (input.get("gender") != null) {
            builder.gender((String) input.get("gender"));
        }
        if (input.get("heartRate") != null) {
            builder.heartRate(((Number) input.get("heartRate")).intValue());
        }
        if (input.get("bloodPressureMax") != null) {
            builder.bloodPressureMax(((Number) input.get("bloodPressureMax")).intValue());
        }
        if (input.get("bloodPressureMin") != null) {
            builder.bloodPressureMin(((Number) input.get("bloodPressureMin")).intValue());
        }
        if (input.get("temperature") != null) {
            builder.temperature(((Number) input.get("temperature")).doubleValue());
        }
        if (input.get("stress") != null) {
            builder.stress(((Number) input.get("stress")).intValue());
        }
        if (input.get("oxygenSaturation") != null) {
            builder.oxygenSaturation(((Number) input.get("oxygenSaturation")).intValue());
        }
        if (input.get("steps") != null) {
            builder.steps(((Number) input.get("steps")).intValue());
        }
        if (input.get("bmi") != null) {
            builder.bmi(((Number) input.get("bmi")).doubleValue());
        }
        if (input.get("totalCholesterol") != null) {
            builder.totalCholesterol(((Number) input.get("totalCholesterol")).doubleValue());
        }
        if (input.get("ldlCholesterol") != null) {
            builder.ldlCholesterol(((Number) input.get("ldlCholesterol")).doubleValue());
        }
        if (input.get("hdlCholesterol") != null) {
            builder.hdlCholesterol(((Number) input.get("hdlCholesterol")).doubleValue());
        }
        if (input.get("triglycerides") != null) {
            builder.triglycerides(((Number) input.get("triglycerides")).doubleValue());
        }
        if (input.get("fastingBloodSugar") != null) {
            builder.fastingBloodSugar(((Number) input.get("fastingBloodSugar")).doubleValue());
        }
        if (input.get("hba1c") != null) {
            builder.hba1c(((Number) input.get("hba1c")).doubleValue());
        }
        if (input.get("predictionHorizonDays") != null) {
            builder.predictionHorizonDays(((Number) input.get("predictionHorizonDays")).intValue());
        }
        if (input.get("topDiseaseCount") != null) {
            builder.topDiseaseCount(((Number) input.get("topDiseaseCount")).intValue());
        }
        
        return builder.build();
    }
}

