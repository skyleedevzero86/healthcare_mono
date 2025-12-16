package com.sleekydz86.service.healthcare.service.ai;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.repository.AIResponseRepository;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIResponseServiceImpl implements AIResponseService {
    private final AIResponseRepository aiResponseRepository;
    private final AIService aiService;
    private final HealthDataValidator healthDataValidator;
    private final AuthServiceClient authServiceClient;

    @Override
    @Cacheable(value = "healthData", key = "#params['userId'] + '_ai_' + #params['date']")
    public ServiceResponse<Map<String, Object>> getAIResponse(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            if (params.get("userSeq") == null) {
                String userId = (String) params.get("userId");
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            Map<String, Object> result = aiResponseRepository.findAIResponse(params);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("AI 응답 조회 중 오류 발생", e);
            return ServiceResponse.error("AI 응답 조회 실패: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Integer> saveAIResponse(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            if (params.get("userSeq") == null) {
                String userId = (String) params.get("userId");
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            int result = aiResponseRepository.saveAIResponse(params);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("AI 응답 저장 중 오류 발생", e);
            return ServiceResponse.error("AI 응답 저장 실패: " + e.getMessage());
        }
    }
}

