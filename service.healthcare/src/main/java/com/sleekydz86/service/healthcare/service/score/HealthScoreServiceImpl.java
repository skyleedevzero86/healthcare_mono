package com.sleekydz86.service.healthcare.service.score;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.dto.ScoreDto;
import com.sleekydz86.service.healthcare.dto.TargetDto;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.HealthScoreRepository;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class HealthScoreServiceImpl implements HealthScoreService {
    private final HealthScoreRepository healthScoreRepository;
    private final HealthDataValidator healthDataValidator;
    private final AuthServiceClient authServiceClient;
    private final HealthcareMetrics healthcareMetrics;

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> calculateSleepScore(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) params.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insSleepScore");

        try {
            healthDataValidator.validate(params);
            log.info("수면 점수 계산 중: {}", userId);

            int score = healthScoreRepository.getSleepScore(params);

            if (params.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) params.get("userSeq"));
            scoreDto.setScoreField("sleep");
            scoreDto.setUserScore(score);
            scoreDto.setDate((String) params.get("date"));

            int result = healthScoreRepository.saveScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("수면 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("수면 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("수면 점수 계산 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> calculateExerciseScore(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) params.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insExerciseScore");

        try {
            healthDataValidator.validate(params);
            log.info("운동 점수 계산 중: {}", userId);

            double personalScore = healthScoreRepository.getWeeklyPersonalExerciseScore(params);
            double criteriaScore = healthScoreRepository.getCriteriaToCalculate(userId);

            double finalScore = (personalScore / (criteriaScore * 7)) * 100;

            if (params.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) params.get("userSeq"));
            scoreDto.setScoreField("exercise");
            scoreDto.setUserScore(finalScore);
            scoreDto.setDate((String) params.get("date"));

            int result = healthScoreRepository.saveScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("운동 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("운동 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("운동 점수 계산 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> calculateStressScore(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) params.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insStressScore");

        try {
            healthDataValidator.validate(params);
            log.info("스트레스 점수 계산 중: {}", userId);

            int score = healthScoreRepository.getStressScore(userId);

            if (params.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) params.get("userSeq"));
            scoreDto.setScoreField("stress");
            scoreDto.setUserScore(score);
            scoreDto.setDate((String) params.get("date"));

            int result = healthScoreRepository.saveScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("스트레스 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("스트레스 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("스트레스 점수 계산 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "healthScore", key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Map<String, Object>> getHealthScoreList(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            Map<String, Object> result = healthScoreRepository.findHealthScoreList(params);
            result.putAll(healthScoreRepository.findHealthScoreInfo((String) params.get("userId")));
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("건강 점수 목록 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthScore", key = "#userId")
    public ServiceResponse<Map<String, Object>> getHealthScoreInfo(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ServiceResponse.error("사용자 ID는 필수입니다");
            }
            Map<String, Object> result = healthScoreRepository.findHealthScoreInfo(userId);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("건강 점수 정보 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthData", key = "#dto.userId + '_target'")
    public ServiceResponse<Map<String, Object>> getTarget(TargetDto dto) {
        try {
            Map<String, Object> result = healthScoreRepository.findTarget(dto);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("목표 정보 조회 실패: " + e.getMessage());
        }
    }
}

