package com.sleekydz86.service.llm.service;

import com.sleekydz86.service.llm.dto.BioInfoDto;
import com.sleekydz86.service.llm.dto.HealthcarePromptRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PromptTemplateService {

    private static final String SYSTEM_PROMPT_HEALTHCARE = """
            당신은 종합병원의 전문의입니다. 사용자의 건강 데이터를 분석하여
            정확하고 실용적인 건강 조언을 제공하는 것이 목표입니다.

            다음 원칙을 따라주세요:
            1. 의학적으로 정확한 정보만 제공
            2. 간결하고 이해하기 쉬운 설명
            3. 개인화된 맞춤 조언
            4. 긍정적이고 격려하는 톤
            5. 필요시 전문의 상담 권장
            """;

    public String buildHealthcarePrompt(HealthcarePromptRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("역할: 사용자들의 바이오 정보로 상태를 파악하고 필요한 진단을 내리는 종합병원 전문의.\n\n");

        prompt.append("사용자 정보:\n");
        if (request.getUserName() != null) {
            prompt.append("- 이름: ").append(request.getUserName()).append("\n");
        }
        if (request.getAge() != null) {
            prompt.append("- 나이: ").append(request.getAge()).append("세\n");
        }

        prompt.append("\n건강 측정 데이터:\n");
        if (request.getHeartRate() != null) {
            prompt.append("- 심박수: ").append(request.getHeartRate()).append(" bpm\n");
        }
        if (request.getBloodPressureMax() != null && request.getBloodPressureMin() != null) {
            prompt.append("- 혈압: ").append(request.getBloodPressureMax())
                    .append("/").append(request.getBloodPressureMin()).append(" mmHg\n");
        }
        if (request.getTemperature() != null) {
            prompt.append("- 체온: ").append(request.getTemperature()).append("°C\n");
        }
        if (request.getStress() != null) {
            prompt.append("- 스트레스 지수: ").append(request.getStress())
                    .append(" (50이 최대 불건강)\n");
        }
        if (request.getOxygenSaturation() != null) {
            prompt.append("- 산소포화도: ").append(request.getOxygenSaturation()).append("%\n");
        }
        if (request.getSteps() != null) {
            prompt.append("- 일일 걸음수: ").append(request.getSteps()).append("보\n");
        }

        if (request.getTotalCholesterol() != null || request.getFastingBloodSugar() != null) {
            prompt.append("\n건강검진 결과:\n");
            if (request.getTotalCholesterol() != null) {
                prompt.append("- 총 콜레스테롤: ").append(request.getTotalCholesterol()).append(" mg/dL\n");
            }
            if (request.getFastingBloodSugar() != null) {
                prompt.append("- 공복혈당: ").append(request.getFastingBloodSugar()).append(" mg/dL\n");
            }
            if (request.getHba1c() != null) {
                prompt.append("- 당화혈색소(HbA1c): ").append(request.getHba1c()).append("%\n");
            }
            if (request.getBmi() != null) {
                prompt.append("- BMI: ").append(request.getBmi()).append("\n");
            }
        }

        if (request.getUserQuestion() != null && !request.getUserQuestion().trim().isEmpty()) {
            prompt.append("\n사용자 질문: ").append(request.getUserQuestion()).append("\n");
        }

        prompt.append("\n다음 내용을 포함하여 응답해주세요:\n");
        int recCount = request.getRecommendationCount() != null ? request.getRecommendationCount() : 3;

        if (request.getIncludeDiseaseRecommendation() != null && request.getIncludeDiseaseRecommendation()) {
            prompt.append("1. 주의해야 할 질병 ").append(recCount).append("가지\n");
        }
        if (request.getIncludeFoodRecommendation() != null && request.getIncludeFoodRecommendation()) {
            prompt.append("2. 해당 질병에 좋은 음식 추천\n");
        }
        if (request.getIncludeExerciseRecommendation() != null && request.getIncludeExerciseRecommendation()) {
            prompt.append("3. 권장 운동 및 생활습관\n");
        }
        if (request.getIncludeDiseaseRecommendation() == null &&
                request.getIncludeFoodRecommendation() == null &&
                request.getIncludeExerciseRecommendation() == null) {
            prompt.append("1. 주의해야 할 질병 ").append(recCount).append("가지\n");
            prompt.append("2. 해당 질병에 좋은 음식 추천\n");
            prompt.append("3. 3줄 정도의 종합 코멘트\n");
        }

        prompt.append("\n응답 형식:\n");
        prompt.append("- 간결하고 정중한 어체 사용\n");
        if (request.getUserName() != null) {
            prompt.append("- 맨 첫 글자는 '").append(request.getUserName()).append("님'으로 시작\n");
        }
        prompt.append("- 주의 질병은 모두 빨간색 처리: <span style=\"color:#F55F5F;\">질병명</span>\n");
        prompt.append("- 추천 음식 키워드는 모두 파란색 처리: <span style=\"color: #325CF0;\">음식명</span>\n");
        prompt.append("- HTML 코드가 아닌 텍스트로 표현\n");

        return prompt.toString();
    }

    public String buildHealthcarePromptFromBioInfo(BioInfoDto bioInfo) {
        HealthcarePromptRequest request = HealthcarePromptRequest.builder()
                .userName(bioInfo.getUserNm())
                .age(bioInfo.getAge())
                .heartRate(bioInfo.getHeartrate())
                .bloodPressureMax(bioInfo.getBloodpressMax())
                .bloodPressureMin(bioInfo.getBloodpressMin())
                .temperature(bioInfo.getTemperature())
                .stress(bioInfo.getStress())
                .oxygenSaturation(bioInfo.getOxygenSaturation())
                .steps(bioInfo.getSteps())
                .includeDiseaseRecommendation(true)
                .includeFoodRecommendation(true)
                .recommendationCount(3)
                .build();

        return buildHealthcarePrompt(request);
    }

    public String buildHealthcarePromptFromMap(Map<String, Object> map) {
        BioInfoDto bioInfo = BioInfoDto.fromMap(map);
        return buildHealthcarePromptFromBioInfo(bioInfo);
    }

    public String getSystemPrompt(String template) {
        if ("healthcare".equals(template)) {
            return SYSTEM_PROMPT_HEALTHCARE;
        }
        return "당신은 도움이 되는 AI 어시스턴트입니다.";
    }

    public String buildGeneralPrompt(String userPrompt, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return userPrompt;
        }

        String result = userPrompt;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }

        return result;
    }
}
