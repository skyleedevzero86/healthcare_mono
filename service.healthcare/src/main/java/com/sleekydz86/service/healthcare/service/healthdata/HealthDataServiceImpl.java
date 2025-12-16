package com.sleekydz86.service.healthcare.service.healthdata;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.dto.HealthData;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.event.HealthDataEvent;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.HealthDataRepository;
import com.sleekydz86.service.healthcare.strategy.DataProcessingService;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HealthDataServiceImpl implements HealthDataService {
    private final HealthDataRepository healthDataRepository;
    private final HealthDataValidator healthDataValidator;
    private final DataProcessingService dataProcessingService;
    private final EventPublisher eventPublisher;
    private final EventStore eventStore;
    private final AuthServiceClient authServiceClient;
    private final HealthcareMetrics healthcareMetrics;
    private final MeterRegistry meterRegistry;
    private final Counter insertCounter;
    private final Timer insertTimer;

    public HealthDataServiceImpl(HealthDataRepository healthDataRepository,
                                HealthDataValidator healthDataValidator,
                                DataProcessingService dataProcessingService,
                                EventPublisher eventPublisher,
                                EventStore eventStore,
                                AuthServiceClient authServiceClient,
                                HealthcareMetrics healthcareMetrics,
                                MeterRegistry meterRegistry) {
        this.healthDataRepository = healthDataRepository;
        this.healthDataValidator = healthDataValidator;
        this.dataProcessingService = dataProcessingService;
        this.eventPublisher = eventPublisher;
        this.eventStore = eventStore;
        this.authServiceClient = authServiceClient;
        this.healthcareMetrics = healthcareMetrics;
        this.meterRegistry = meterRegistry;
        this.insertCounter = Counter.builder("healthdata.insert.count")
                .description("건강 데이터 삽입 횟수")
                .register(meterRegistry);
        this.insertTimer = Timer.builder("healthdata.insert.duration")
                .description("건강 데이터 삽입 소요 시간")
                .register(meterRegistry);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthInfo", "healthChart", "healthData"}, allEntries = true)
    public ServiceResponse<Integer> insertMinuteData(MinuteDataDto dto) {
        return insertTimer.recordCallable(() -> {
            insertCounter.increment();
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
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    @CacheEvict(value = {"healthInfo", "healthChart", "healthData"}, allEntries = true)
    public CompletableFuture<ServiceResponse<Integer>> insertMinuteDataAsync(MinuteDataDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            insertCounter.increment();
            String requestId = UUID.randomUUID().toString();
            MDC.put("userId", dto.getUserId() != null ? dto.getUserId() : "unknown");
            MDC.put("requestId", requestId);
            MDC.put("operation", "insMinuteDataAsync");

            Timer.Sample sample = healthcareMetrics.startHealthDataProcessingTimer();

            try {
                healthDataValidator.validate(dto);
                log.info("분 단위 건강 데이터 비동기 처리 중: {}", dto.getUserId());

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

                    log.info("분 단위 건강 데이터 비동기 처리 완료: {}, 결과: {}", dto.getUserId(), result);
                }

                return ServiceResponse.success(result);
            } catch (ValidationException e) {
                log.error("검증 실패: {}", e.getMessage());
                return ServiceResponse.error("검증 실패: " + e.getMessage());
            } catch (Exception e) {
                log.error("분 단위 건강 데이터 비동기 처리 중 오류 발생: {}", dto.getUserId(), e);
                return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
            } finally {
                sample.stop(healthcareMetrics.getHealthDataProcessingTime());
                MDC.clear();
            }
        });
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
    @Cacheable(value = "healthData", key = "#params.hashCode()")
    public ServiceResponse<List<HealthData>> getHealthInfo(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = params.get("userId") != null ? params.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "healthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            healthDataValidator.validate(params);
            log.info("건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, params.get("date"));
            List<Map<String, Object>> rawResult = healthDataRepository.findHealthInfo(params);
            List<HealthData> result = rawResult.stream()
                    .map(HealthData::new)
                    .toList();
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
    @Cacheable(value = "healthData", key = "#params.hashCode() + '_minmax'")
    public ServiceResponse<List<HealthData>> getMinMaxHealthInfo(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = params.get("userId") != null ? params.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "minmaxHealthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            healthDataValidator.validate(params);
            log.info("최소/최대 건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, params.get("date"));
            List<Map<String, Object>> rawResult = healthDataRepository.findMinMaxHealthInfo(params);
            List<HealthData> result = rawResult.stream()
                    .map(HealthData::new)
                    .toList();
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
            log.info("건강 정보 테스트 데이터 삽입 중: {}", dto.getUserId());
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
                log.error("사용자 시퀀스를 찾을 수 없음: {}", dto.getUserId());
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.insertHealthInfoTest(dto);
            log.info("건강 정보 테스트 데이터 삽입 완료: {}, 결과: {}", dto.getUserId(), result);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("건강 정보 테스트 데이터 삽입 중 오류 발생: {}", dto.getUserId(), e);
            return ServiceResponse.error("삽입 실패: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Integer> testInsertMinute(TestDto dto) {
        try {
            healthDataValidator.validate(dto);
            log.info("분 단위 테스트 데이터 삽입 중: {}", dto.getUserId());
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
                log.error("사용자 시퀀스를 찾을 수 없음: {}", dto.getUserId());
                return ServiceResponse.error("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthDataRepository.testInsertMinute(dto);
            log.info("분 단위 테스트 데이터 삽입 완료: {}, 결과: {}", dto.getUserId(), result);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("분 단위 테스트 데이터 삽입 중 오류 발생: {}", dto.getUserId(), e);
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthData", "healthInfo"}, key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> insertDailyStep(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            log.info("일일 걸음수 데이터 삽입 중: {}", params.get("userId"));
            Map<String, Object> processedData = dataProcessingService.processData(params);

            int result = 0;
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) processedData.get("data");

            if (dataList == null || dataList.isEmpty()) {
                log.error("데이터 목록이 비어있음: {}", params.get("userId"));
                return ServiceResponse.error("데이터 목록이 비어있습니다");
            }

            for (Map<String, Object> data : dataList) {
                params.forEach((key, value) -> {
                    if (!"data".equals(key)) {
                        data.put(key, value);
                    }
                });
                result += healthDataRepository.insertDailyStep(data);
            }

            log.info("일일 걸음수 데이터 삽입 완료: {}, 결과: {}", params.get("userId"), result);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("일일 걸음수 데이터 삽입 중 오류 발생: {}", params.get("userId"), e);
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"healthData", "healthInfo"}, key = "#params['userId'] + '_' + #params['date']")
    public ServiceResponse<Integer> insertDailySleep(Map<String, Object> params) {
        try {
            healthDataValidator.validate(params);
            log.info("일일 수면 데이터 삽입 중: {}", params.get("userId"));
            Map<String, Object> processedData = dataProcessingService.processData(params);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) processedData.get("data");
            
            if (dataList == null || dataList.isEmpty()) {
                log.error("데이터 목록이 비어있음: {}", params.get("userId"));
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

            log.info("일일 수면 데이터 삽입 완료: {}, 결과: {}", params.get("userId"), result);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            log.error("검증 실패: {}", e.getMessage());
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("일일 수면 데이터 삽입 중 오류 발생: {}", params.get("userId"), e);
            return ServiceResponse.error("데이터 삽입 실패: " + e.getMessage());
        }
    }
}
