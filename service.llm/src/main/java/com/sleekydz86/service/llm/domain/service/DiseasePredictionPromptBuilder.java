package com.sleekydz86.service.llm.domain.service;

import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.dto.DiseasePredictionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DiseasePredictionPromptBuilder {

    private static final String SYSTEM_PROMPT_DISEASE_PREDICTION = """
            당신은 질병 예측 전문 AI 시스템입니다. 사용자의 건강 데이터를 분석하여
            미래 질병 발생 가능성을 정확하게 예측하는 것이 목표입니다.
            
            다음 원칙을 따라주세요:
            1. 의학적으로 검증된 데이터 기반 예측
            2. 확률 기반 위험도 평가
            3. 객관적이고 정확한 분석
            4. 예방 조치 제안
            5. JSON 형식으로 구조화된 응답
            """;

    public Prompt buildDiseasePredictionPrompt(DiseasePredictionRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("역할: 질병 예측 전문 AI 시스템\n\n");
        prompt.append("사용자 정보:\n");
        appendIfNotNull(prompt, "- 사용자 ID: ", request.getUserId(), "\n");
        appendIfNotNull(prompt, "- 이름: ", request.getUserName(), "\n");
        appendIfNotNull(prompt, "- 나이: ", request.getAge(), "세\n");
        appendIfNotNull(prompt, "- 성별: ", request.getGender(), "\n");
        
        prompt.append("\n생체 신호 데이터:\n");
        appendIfNotNull(prompt, "- 심박수: ", request.getHeartRate(), " bpm\n");
        appendBloodPressure(prompt, request);
        appendIfNotNull(prompt, "- 체온: ", request.getTemperature(), "°C\n");
        appendIfNotNull(prompt, "- 스트레스 지수: ", request.getStress(), "\n");
        appendIfNotNull(prompt, "- 산소포화도: ", request.getOxygenSaturation(), "%\n");
        appendIfNotNull(prompt, "- 일일 걸음수: ", request.getSteps(), "보\n");
        
        prompt.append("\n혈액 검사 결과:\n");
        appendIfNotNull(prompt, "- 총 콜레스테롤: ", request.getTotalCholesterol(), " mg/dL\n");
        appendIfNotNull(prompt, "- LDL 콜레스테롤: ", request.getLdlCholesterol(), " mg/dL\n");
        appendIfNotNull(prompt, "- HDL 콜레스테롤: ", request.getHdlCholesterol(), " mg/dL\n");
        appendIfNotNull(prompt, "- 중성지방: ", request.getTriglycerides(), " mg/dL\n");
        appendIfNotNull(prompt, "- 공복혈당: ", request.getFastingBloodSugar(), " mg/dL\n");
        appendIfNotNull(prompt, "- 당화혈색소(HbA1c): ", request.getHba1c(), "%\n");
        appendIfNotNull(prompt, "- BMI: ", request.getBmi(), "\n");
        
        appendSymptoms(prompt, request.getSymptoms());
        appendFamilyHistory(prompt, request.getFamilyHistory());
        appendCurrentMedications(prompt, request.getCurrentMedications());
        appendLifestyle(prompt, request);
        
        prompt.append("\n예측 요청 사항:\n");
        appendIfNotNull(prompt, "- 예측 기간: ", request.getPredictionHorizonDays(), "일 후\n");
        appendIfNotNull(prompt, "- 상위 질병 개수: ", request.getTopDiseaseCount(), "개\n");
        
        prompt.append("\n응답 형식:\n");
        prompt.append("다음 JSON 형식으로 응답해주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"overallRiskLevel\": \"LOW|MEDIUM|HIGH|CRITICAL\",\n");
        prompt.append("  \"overallRiskScore\": 0.0-1.0,\n");
        prompt.append("  \"summary\": \"전체 위험도 요약 설명\",\n");
        prompt.append("  \"predictedDiseases\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"diseaseName\": \"질병명\",\n");
        prompt.append("      \"diseaseCode\": \"ICD-10 코드\",\n");
        prompt.append("      \"probability\": 0.0-1.0,\n");
        prompt.append("      \"riskLevel\": \"LOW|MEDIUM|HIGH|CRITICAL\",\n");
        prompt.append("      \"description\": \"질병 설명\",\n");
        prompt.append("      \"riskFactors\": [\"위험요인1\", \"위험요인2\"],\n");
        prompt.append("      \"preventiveMeasures\": [\"예방조치1\", \"예방조치2\"],\n");
        prompt.append("      \"estimatedOnsetDays\": 예상 발병일수,\n");
        prompt.append("      \"severity\": \"MILD|MODERATE|SEVERE\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"generalRecommendations\": [\"권장사항1\", \"권장사항2\"]\n");
        prompt.append("}\n");
        
        prompt.append("\n중요 사항:\n");
        prompt.append("- 모든 확률 값은 0.0과 1.0 사이의 실수여야 합니다.\n");
        prompt.append("- predictedDiseases는 확률이 높은 순서로 정렬해주세요.\n");
        prompt.append("- JSON 형식만 응답하고 다른 텍스트는 포함하지 마세요.\n");
        prompt.append("- 의학적으로 검증된 정보만 제공하세요.\n");
        
        return Prompt.builder()
                .content(prompt.toString())
                .template("disease-prediction")
                .systemPrompt(SYSTEM_PROMPT_DISEASE_PREDICTION)
                .build();
    }

    private void appendIfNotNull(StringBuilder prompt, String prefix, Object value, String suffix) {
        if (value != null) {
            prompt.append(prefix).append(value).append(suffix);
        }
    }

    private void appendBloodPressure(StringBuilder prompt, DiseasePredictionRequest request) {
        if (request.getBloodPressureMax() != null && request.getBloodPressureMin() != null) {
            prompt.append("- 혈압: ")
                  .append(request.getBloodPressureMax())
                  .append("/")
                  .append(request.getBloodPressureMin())
                  .append(" mmHg\n");
        }
    }

    private void appendSymptoms(StringBuilder prompt, List<String> symptoms) {
        if (symptoms != null && !symptoms.isEmpty()) {
            prompt.append("\n현재 증상:\n");
            for (String symptom : symptoms) {
                prompt.append("- ").append(symptom).append("\n");
            }
        }
    }

    private void appendFamilyHistory(StringBuilder prompt, List<String> familyHistory) {
        if (familyHistory != null && !familyHistory.isEmpty()) {
            prompt.append("\n가족력:\n");
            for (String history : familyHistory) {
                prompt.append("- ").append(history).append("\n");
            }
        }
    }

    private void appendCurrentMedications(StringBuilder prompt, List<String> medications) {
        if (medications != null && !medications.isEmpty()) {
            prompt.append("\n현재 복용 중인 약물:\n");
            for (String medication : medications) {
                prompt.append("- ").append(medication).append("\n");
            }
        }
    }

    private void appendLifestyle(StringBuilder prompt, DiseasePredictionRequest request) {
        if (request.getLifestyle() != null || 
            request.getSmokingYears() != null || 
            request.getAlcoholFrequency() != null) {
            prompt.append("\n생활습관:\n");
            appendIfNotNull(prompt, "- 생활 패턴: ", request.getLifestyle(), "\n");
            appendIfNotNull(prompt, "- 흡연 기간: ", request.getSmokingYears(), "년\n");
            appendIfNotNull(prompt, "- 음주 빈도: ", request.getAlcoholFrequency(), "회/주\n");
        }
    }
}

