package com.sleekydz86.service.llm.domain.service;

import com.sleekydz86.service.llm.domain.model.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthcarePromptBuilder implements PromptBuilder {

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

    @Override
    public Prompt buildHealthcarePrompt(Map<String, Object> bioInfo) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("역할: 사용자들의 바이오 정보로 상태를 파악하고 필요한 진단을 내리는 종합병원 전문의.\n\n");

        prompt.append("사용자 정보:\n");
        if (bioInfo.get("userNm") != null) {
            prompt.append("- 이름: ").append(bioInfo.get("userNm")).append("\n");
        }
        if (bioInfo.get("age") != null) {
            prompt.append("- 나이: ").append(bioInfo.get("age")).append("세\n");
        }

        prompt.append("\n건강 측정 데이터:\n");
        appendIfNotNull(prompt, "- 심박수: ", bioInfo.get("heartrate"), " bpm\n");
        appendBloodPressure(prompt, bioInfo);
        appendIfNotNull(prompt, "- 체온: ", bioInfo.get("temperature"), "°C\n");
        appendIfNotNull(prompt, "- 스트레스 지수: ", bioInfo.get("stress"), " (50이 최대 불건강)\n");
        appendIfNotNull(prompt, "- 산소포화도: ", bioInfo.get("oxygenSaturation"), "%\n");
        appendIfNotNull(prompt, "- 일일 걸음수: ", bioInfo.get("steps"), "보\n");

        appendHealthCheckResults(prompt, bioInfo);
        appendUserQuestion(prompt, bioInfo);
        appendRecommendations(prompt, bioInfo);
        appendResponseFormat(prompt, bioInfo);

        return Prompt.builder()
                .content(prompt.toString())
                .template("healthcare")
                .variables(bioInfo)
                .systemPrompt(SYSTEM_PROMPT_HEALTHCARE)
                .build();
    }

    @Override
    public Prompt buildGeneralPrompt(String userPrompt, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return Prompt.builder()
                    .content(userPrompt)
                    .build();
        }

        String result = userPrompt;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }

        return Prompt.builder()
                .content(result)
                .variables(variables)
                .build();
    }

    @Override
    public String getSystemPrompt(String template) {
        if ("healthcare".equals(template)) {
            return SYSTEM_PROMPT_HEALTHCARE;
        }
        return "당신은 도움이 되는 AI 어시스턴트입니다.";
    }

    private void appendIfNotNull(StringBuilder prompt, String prefix, Object value, String suffix) {
        if (value != null) {
            prompt.append(prefix).append(value).append(suffix);
        }
    }

    private void appendBloodPressure(StringBuilder prompt, Map<String, Object> bioInfo) {
        Object max = bioInfo.get("bloodpressMax");
        Object min = bioInfo.get("bloodpressMin");
        if (max != null && min != null) {
            prompt.append("- 혈압: ").append(max).append("/").append(min).append(" mmHg\n");
        }
    }

    private void appendHealthCheckResults(StringBuilder prompt, Map<String, Object> bioInfo) {
        if (bioInfo.get("totalCholesterol") != null || bioInfo.get("fastingBloodSugar") != null) {
            prompt.append("\n건강검진 결과:\n");
            appendIfNotNull(prompt, "- 총 콜레스테롤: ", bioInfo.get("totalCholesterol"), " mg/dL\n");
            appendIfNotNull(prompt, "- 공복혈당: ", bioInfo.get("fastingBloodSugar"), " mg/dL\n");
            appendIfNotNull(prompt, "- 당화혈색소(HbA1c): ", bioInfo.get("hba1c"), "%\n");
            appendIfNotNull(prompt, "- BMI: ", bioInfo.get("bmi"), "\n");
        }
    }

    private void appendUserQuestion(StringBuilder prompt, Map<String, Object> bioInfo) {
        Object question = bioInfo.get("userQuestion");
        if (question != null && question.toString().trim().length() > 0) {
            prompt.append("\n사용자 질문: ").append(question).append("\n");
        }
    }

    private void appendRecommendations(StringBuilder prompt, Map<String, Object> bioInfo) {
        prompt.append("\n다음 내용을 포함하여 응답해주세요:\n");
        int recCount = bioInfo.get("recommendationCount") != null
                ? Integer.parseInt(bioInfo.get("recommendationCount").toString())
                : 3;

        Boolean includeDisease = getBoolean(bioInfo, "includeDiseaseRecommendation");
        Boolean includeFood = getBoolean(bioInfo, "includeFoodRecommendation");
        Boolean includeExercise = getBoolean(bioInfo, "includeExerciseRecommendation");

        if (includeDisease != null && includeDisease) {
            prompt.append("1. 주의해야 할 질병 ").append(recCount).append("가지\n");
        }
        if (includeFood != null && includeFood) {
            prompt.append("2. 해당 질병에 좋은 음식 추천\n");
        }
        if (includeExercise != null && includeExercise) {
            prompt.append("3. 권장 운동 및 생활습관\n");
        }
        if (includeDisease == null && includeFood == null && includeExercise == null) {
            prompt.append("1. 주의해야 할 질병 ").append(recCount).append("가지\n");
            prompt.append("2. 해당 질병에 좋은 음식 추천\n");
            prompt.append("3. 3줄 정도의 종합 코멘트\n");
        }
    }

    private void appendResponseFormat(StringBuilder prompt, Map<String, Object> bioInfo) {
        prompt.append("\n응답 형식:\n");
        prompt.append("- 간결하고 정중한 어체 사용\n");
        if (bioInfo.get("userNm") != null) {
            prompt.append("- 맨 첫 글자는 '").append(bioInfo.get("userNm")).append("님'으로 시작\n");
        }
        prompt.append("- 주의 질병은 모두 빨간색 처리: <span style=\"color:#F55F5F;\">질병명</span>\n");
        prompt.append("- 추천 음식 키워드는 모두 파란색 처리: <span style=\"color: #325CF0;\">음식명</span>\n");
        prompt.append("- HTML 코드가 아닌 텍스트로 표현\n");
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
}
