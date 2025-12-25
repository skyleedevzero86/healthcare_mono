package com.sleekydz86.service.llm.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.domain.model.DiseasePrediction;
import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.domain.service.DiseasePredictionPromptBuilder;
import com.sleekydz86.service.llm.dto.DiseasePredictionRequest;
import com.sleekydz86.service.llm.dto.DiseasePredictionResponse;
import com.sleekydz86.service.llm.dto.PredictedDisease;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiseasePredictionService {

    private final GenerateLLMUseCase generateLLMUseCase;
    private final DiseasePredictionPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public DiseasePredictionResponse predict(DiseasePredictionRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (!generateLLMUseCase.isAvailable()) {
                throw new RuntimeException("LLM 서비스가 현재 사용할 수 없습니다.");
            }

            Prompt prompt = promptBuilder.buildDiseasePredictionPrompt(request);
            
            LLMGenerationRequest llmRequest = LLMGenerationRequest.builder()
                    .prompt(prompt)
                    .maxTokens(4096)
                    .temperature(0.3)
                    .topP(0.9)
                    .topK(40)
                    .repeatPenalty(1.1)
                    .stream(false)
                    .requestType("disease-prediction")
                    .build();

            LLMGenerationResult result = generateLLMUseCase.generate(llmRequest);
            
            DiseasePrediction prediction = parseLLMResponse(result.getContent(), request);
            
            return buildResponse(prediction, request, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("질병 예측 처리 중 오류 발생", e);
            throw new RuntimeException("질병 예측 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private DiseasePrediction parseLLMResponse(String llmResponse, DiseasePredictionRequest request) {
        try {
            String cleanedResponse = cleanJsonResponse(llmResponse);
            JsonNode rootNode = objectMapper.readTree(cleanedResponse);
            
            DiseasePrediction prediction = DiseasePrediction.builder()
                    .userId(request.getUserId())
                    .predictionDate(LocalDateTime.now())
                    .predictionHorizonDays(request.getPredictionHorizonDays() != null 
                            ? request.getPredictionHorizonDays() : 90)
                    .overallRiskLevel(rootNode.path("overallRiskLevel").asText("MEDIUM"))
                    .overallRiskScore(rootNode.path("overallRiskScore").asDouble(0.5))
                    .summary(rootNode.path("summary").asText(""))
                    .predictedDiseases(parsePredictedDiseases(rootNode.path("predictedDiseases")))
                    .generalRecommendations(parseStringList(rootNode.path("generalRecommendations")))
                    .healthMetrics(buildHealthMetrics(request))
                    .build();
            
            return prediction;
            
        } catch (Exception e) {
            log.error("LLM 응답 파싱 오류", e);
            return createDefaultPrediction(request);
        }
    }

    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        
        int jsonStart = cleaned.indexOf("{");
        int jsonEnd = cleaned.lastIndexOf("}");
        
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }
        
        cleaned = cleaned.replaceAll("```json", "");
        cleaned = cleaned.replaceAll("```", "");
        cleaned = cleaned.trim();
        
        return cleaned;
    }

    private List<DiseasePrediction.PredictedDiseaseInfo> parsePredictedDiseases(JsonNode diseasesNode) {
        List<DiseasePrediction.PredictedDiseaseInfo> diseases = new ArrayList<>();
        
        if (diseasesNode.isArray()) {
            for (JsonNode diseaseNode : diseasesNode) {
                DiseasePrediction.PredictedDiseaseInfo disease = DiseasePrediction.PredictedDiseaseInfo.builder()
                        .diseaseName(diseaseNode.path("diseaseName").asText(""))
                        .diseaseCode(diseaseNode.path("diseaseCode").asText(""))
                        .probability(diseaseNode.path("probability").asDouble(0.0))
                        .riskLevel(diseaseNode.path("riskLevel").asText("MEDIUM"))
                        .description(diseaseNode.path("description").asText(""))
                        .riskFactors(parseStringList(diseaseNode.path("riskFactors")))
                        .preventiveMeasures(parseStringList(diseaseNode.path("preventiveMeasures")))
                        .estimatedOnsetDays(diseaseNode.path("estimatedOnsetDays").asInt(0))
                        .severity(diseaseNode.path("severity").asText("MODERATE"))
                        .build();
                
                diseases.add(disease);
            }
        }
        
        return diseases;
    }

    private List<String> parseStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isTextual()) {
                    list.add(item.asText());
                }
            }
        }
        
        return list;
    }

    private Map<String, Object> buildHealthMetrics(DiseasePredictionRequest request) {
        Map<String, Object> metrics = new HashMap<>();
        
        if (request.getHeartRate() != null) {
            metrics.put("heartRate", request.getHeartRate());
        }
        if (request.getBloodPressureMax() != null) {
            metrics.put("bloodPressureMax", request.getBloodPressureMax());
        }
        if (request.getBloodPressureMin() != null) {
            metrics.put("bloodPressureMin", request.getBloodPressureMin());
        }
        if (request.getTemperature() != null) {
            metrics.put("temperature", request.getTemperature());
        }
        if (request.getBmi() != null) {
            metrics.put("bmi", request.getBmi());
        }
        if (request.getTotalCholesterol() != null) {
            metrics.put("totalCholesterol", request.getTotalCholesterol());
        }
        if (request.getFastingBloodSugar() != null) {
            metrics.put("fastingBloodSugar", request.getFastingBloodSugar());
        }
        
        return metrics;
    }

    private DiseasePrediction createDefaultPrediction(DiseasePredictionRequest request) {
        return DiseasePrediction.builder()
                .userId(request.getUserId())
                .predictionDate(LocalDateTime.now())
                .predictionHorizonDays(request.getPredictionHorizonDays() != null 
                        ? request.getPredictionHorizonDays() : 90)
                .overallRiskLevel("MEDIUM")
                .overallRiskScore(0.5)
                .summary("데이터 분석 중 오류가 발생했습니다. 다시 시도해주세요.")
                .predictedDiseases(new ArrayList<>())
                .generalRecommendations(List.of("정기적인 건강검진을 받으시기 바랍니다."))
                .healthMetrics(buildHealthMetrics(request))
                .build();
    }

    private DiseasePredictionResponse buildResponse(
            DiseasePrediction prediction, 
            DiseasePredictionRequest request, 
            long processingTimeMs) {
        
        List<PredictedDisease> predictedDiseases = new ArrayList<>();
        
        if (prediction.getPredictedDiseases() != null) {
            for (DiseasePrediction.PredictedDiseaseInfo info : prediction.getPredictedDiseases()) {
                PredictedDisease disease = PredictedDisease.builder()
                        .diseaseName(info.getDiseaseName())
                        .diseaseCode(info.getDiseaseCode())
                        .probability(info.getProbability())
                        .riskLevel(info.getRiskLevel())
                        .description(info.getDescription())
                        .riskFactors(info.getRiskFactors())
                        .preventiveMeasures(info.getPreventiveMeasures())
                        .estimatedOnsetDays(info.getEstimatedOnsetDays())
                        .severity(info.getSeverity())
                        .build();
                
                predictedDiseases.add(disease);
            }
        }
        
        return DiseasePredictionResponse.builder()
                .userId(prediction.getUserId())
                .predictionDate(prediction.getPredictionDate())
                .predictionHorizonDays(prediction.getPredictionHorizonDays())
                .overallRiskLevel(prediction.getOverallRiskLevel())
                .overallRiskScore(prediction.getOverallRiskScore())
                .predictedDiseases(predictedDiseases)
                .summary(prediction.getSummary())
                .generalRecommendations(prediction.getGeneralRecommendations())
                .processingTimeMs(processingTimeMs)
                .build();
    }
}

