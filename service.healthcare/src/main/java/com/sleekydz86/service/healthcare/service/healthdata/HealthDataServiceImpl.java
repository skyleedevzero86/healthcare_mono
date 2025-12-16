package com.sleekydz86.service.healthcare.service.healthdata;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.event.HealthDataEvent;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.HealthDataRepository;
import com.sleekydz86.service.healthcare.strategy.DataProcessingService;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataServiceImpl implements HealthDataService {
    private final HealthDataRepository healthDataRepository;
    private final HealthDataValidator healthDataValidator;
    private final DataProcessingService dataProcessingService;
    private final EventPublisher eventPublisher;
    private final EventStore eventStore;
    private final AuthServiceClient authServiceClient;
    private final HealthcareMetrics healthcareMetrics;

    @Override
    @Transactional
    @CacheEvict(value = {"healthInfo", "healthChart", "healthData"}, allEntries = true)
    public ServiceResponse<Integer> insertMinuteData(MinuteDataDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", dto.getUserId() != null ? dto.getUserId() : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insMinuteData");

        Timer.Sample sample = healthcareMetrics.startHealthDataProcessingTimer();

        try {
            healthDataValidator.validate(dto);
            log.info("분 단위 건강 데이터 처리 중: {}", dto.getUserId());

            if (dto.getUserSeq() == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", dto.getUserId());
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        dto.setUserSeq(((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            if (dto.getUserSeq() == null) {
                log.error("사용자 시퀀스를 찾을 수 없음: {}", dto.getUserId());
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.insertMinuteData(dto);

            if (result > 0) {
                healthcareMetrics.incrementHealthDataProcessed();
                healthcareMetrics.incrementHealthDataProcessedMinute();

                HealthDataEvent event = new HealthDataEvent(
                        UUID.randomUUID().toString(),
                        "INSERT",
                        dto.getUserId(),
                        "MINUTE",
                        dto,
                        LocalDateTime.now(),
                        "service.healthcare");
                eventStore.saveEvent(event);
                eventPublisher.publishHealthDataEvent(event);

                log.info("분 단위 건강 데이터 처리 완료: {}, 결과: {}", dto.getUserId(), result);
            }

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("분 단위 건강 데이터 처리 중 오류 발생: {}", dto.getUserId(), e);
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        } finally {
            sample.stop(healthcareMetrics.getHealthDataProcessingTime());
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthInfo", "healthChart", "healthData"}, allEntries = true)
    public ServiceResponse<Integer> insertMonthDayData(MonthDayDataDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", dto.getUserId() != null ? dto.getUserId() : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insMonthDayData");

        Timer.Sample sample = healthcareMetrics.startHealthDataProcessingTimer();

        try {
            healthDataValidator.validate(dto);
            log.info("일일 건강 데이터 처리 중: {}", dto.getUserId());

            if (dto.getUserSeq() == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", dto.getUserId());
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        dto.setUserSeq(((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            if (dto.getUserSeq() == null) {
                log.error("사용자 시퀀스를 찾을 수 없음: {}", dto.getUserId());
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.insertMonthDayData(dto);

            if (result > 0) {
                healthcareMetrics.incrementHealthDataProcessed();
                healthcareMetrics.incrementHealthDataProcessedDaily();

                HealthDataEvent event = new HealthDataEvent(
                        UUID.randomUUID().toString(),
                        "INSERT",
                        dto.getUserId(),
                        "DAILY",
                        dto,
                        LocalDateTime.now(),
                        "service.healthcare");
                eventStore.saveEvent(event);
                eventPublisher.publishHealthDataEvent(event);

                log.info("일일 건강 데이터 처리 완료: {}, 결과: {}", dto.getUserId(), result);
            }

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("일일 건강 데이터 처리 중 오류 발생: {}", dto.getUserId(), e);
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        } finally {
            sample.stop(healthcareMetrics.getHealthDataProcessingTime());
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "healthInfo", key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<List<Map<String, Object>>> getHealthInfo(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = params.get("userId") != null ? params.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "healthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            healthDataValidator.validate(params);
            log.info("건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, params.get("date"));
            List<Map<String, Object>> result = healthDataRepository.findHealthInfo(params);
            log.info("건강 정보 조회 완료: 사용자 {}, 결과 크기: {}", userId, result != null ? result.size() : 0);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("조회 실패: " + e.getMessage());
        } finally {
            sample.stop(healthcareMetrics.getHealthInfoQueryTime());
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "healthInfo", key = "#params['userId'] + '_' + #params['date'] + '_minmax'")
    public ServiceResponse<List<Map<String, Object>>> getMinMaxHealthInfo(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = params.get("userId") != null ? params.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "minmaxHealthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            healthDataValidator.validate(params);
            log.info("최소/최대 건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, params.get("date"));
            List<Map<String, Object>> result = healthDataRepository.findMinMaxHealthInfo(params);
            log.info("최소/최대 건강 정보 조회 완료: 사용자 {}, 결과 크기: {}", userId, result != null ? result.size() : 0);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("최소/최대 건강 정보 조회 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("조회 실패: " + e.getMessage());
        } finally {
            sample.stop(healthcareMetrics.getHealthInfoQueryTime());
            MDC.clear();
        }
    }

    @Override
    @Transactional
    public ServiceResponse<Integer> insertHealthInfoTest(TestDto dto) {
        try {
            healthDataValidator.validate(dto);
            if (dto.getUserSeq() == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", dto.getUserId());
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        dto.setUserSeq(((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            if (dto.getUserSeq() == null) {
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.insertHealthInfoTest(dto);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("삽입 실패: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Integer> testInsertMinute(TestDto dto) {
        try {
            healthDataValidator.validate(dto);
            if (dto.getUserSeq() == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", dto.getUserId());
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        dto.setUserSeq(((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            if (dto.getUserSeq() == null) {
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.testInsertMinute(dto);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthData", "healthInfo"}, key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> insertDailyStep(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) params.get("data");
            if (dataList == null || dataList.isEmpty()) {
                return ServiceResponse.error("데이터 목록이 비어있습니다");
            }

            for (Map<String, Object> data : dataList) {
                dataProcessingService.processData(data);
                params.forEach((key, value) -> {
                    if (!"data".equals(key)) {
                        data.put(key, value);
                    }
                });
            }

            int result = 0;
            for (Map<String, Object> data : dataList) {
                result += healthDataRepository.insertDailyStep(data);
            }

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthData", "healthInfo"}, key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> insertDailySleep(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) params.get("data");
            if (dataList == null || dataList.isEmpty()) {
                return ServiceResponse.error("데이터 목록이 비어있습니다");
            }

            for (Map<String, Object> data : dataList) {
                dataProcessingService.processData(data);
                params.forEach((key, value) -> {
                    if (!"data".equals(key)) {
                        data.put(key, value);
                    }
                });
            }

            int result = 0;
            for (Map<String, Object> data : dataList) {
                result += healthDataRepository.insertDailySleep(data);
            }

            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<List<Map<String, Object>>> selectList(Map<String, Object> map) {
        try {
            List<Map<String, Object>> result = healthDataRepository.selectList(map);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("조회 실패: " + e.getMessage());
        }
    }
}

