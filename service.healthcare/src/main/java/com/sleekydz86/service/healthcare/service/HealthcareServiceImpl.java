package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.event.HealthDataEvent;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import com.sleekydz86.service.healthcare.global.util.HealthcareEncryptionUtil;
import com.sleekydz86.service.healthcare.global.util.HealthcareEncryptionUtil.KeyType;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HealthcareServiceImpl implements HealthcareService {

    private final HealthcareMapper healthcareMapper;
    private final EventPublisher eventPublisher;
    private final EventStore eventStore;
    private final com.sleekydz86.service.healthcare.client.AuthServiceClient authServiceClient;
    private final HealthcareMetrics healthcareMetrics;

    public List<Map<String, Object>> selectList(Map<String, Object> map) {
        return healthcareMapper.selectList(map);
    }

    @Transactional
    @CacheEvict(value = { "healthInfo", "healthChart", "healthData" }, allEntries = true)
    public int insMinuteData(MinuteDataDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", dto.getUserId() != null ? dto.getUserId() : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insMinuteData");

        Timer.Sample sample = healthcareMetrics.startHealthDataProcessingTimer();

        try {
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
                throw new IllegalStateException("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }

            if (dto.getSpo2() != 0) {
                dto.setSpo2Enc(HealthcareEncryptionUtil.encrypt(
                    String.valueOf(dto.getSpo2()),
                    KeyType.HEALTH
                ));
            }
            if (dto.getHeartrate() != 0) {
                dto.setHeartrateEnc(HealthcareEncryptionUtil.encrypt(
                    String.valueOf(dto.getHeartrate()),
                    KeyType.HEALTH
                ));
            }
            if (dto.getBloodpressMin() != 0 || dto.getBloodpressMax() != 0) {
                String bloodpress = dto.getBloodpressMin() + "/" + dto.getBloodpressMax();
                dto.setBloodpressEnc(HealthcareEncryptionUtil.encrypt(
                    bloodpress,
                    KeyType.HEALTH
                ));
            }

            int result = healthcareMapper.insMinuteData(dto);

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

            return result;
        } catch (Exception e) {
            log.error("분 단위 건강 데이터 처리 중 오류 발생: {}", dto.getUserId(), e);
            throw e;
        } finally {
            sample.stop(healthcareMetrics.getHealthDataProcessingTime());
            MDC.clear();
        }
    }

    @Transactional
    @CacheEvict(value = { "healthInfo", "healthChart", "healthData" }, allEntries = true)
    public int insMonthDayData(MonthDayDataDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", dto.getUserId() != null ? dto.getUserId() : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insMonthDayData");

        Timer.Sample sample = healthcareMetrics.startHealthDataProcessingTimer();

        try {
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
                throw new IllegalStateException("사용자 시퀀스를 찾을 수 없습니다. 사용자 ID: " + dto.getUserId());
            }
            int result = healthcareMapper.insMonthDayData(dto);

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

            return result;
        } catch (Exception e) {
            log.error("일일 건강 데이터 처리 중 오류 발생: {}", dto.getUserId(), e);
            throw e;
        } finally {
            sample.stop(healthcareMetrics.getHealthDataProcessingTime());
            MDC.clear();
        }
    }

    @Cacheable(value = "healthInfo", key = "#map['userId'] + '_' + #map['date'] + '_minmax'")
    public List<Map<String, Object>> minmaxHealthInfo(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = map.get("userId") != null ? map.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "minmaxHealthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            log.info("최소/최대 건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, map.get("date"));
            List<Map<String, Object>> result = healthcareMapper.minmaxHealthInfo(map);
            log.info("최소/최대 건강 정보 조회 완료: 사용자 {}, 결과 크기: {}", userId, result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("최소/최대 건강 정보 조회 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            sample.stop(healthcareMetrics.getHealthInfoQueryTime());
            MDC.clear();
        }
    }

    @Cacheable(value = "healthInfo", key = "#map['userId'] + '_' + #map['date']")
    public List<Map<String, Object>> healthInfo(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = map.get("userId") != null ? map.get("userId").toString() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "healthInfo");

        Timer.Sample sample = healthcareMetrics.startHealthInfoQueryTimer();

        try {
            log.info("건강 정보 조회 중: 사용자 {}, 날짜 {}", userId, map.get("date"));
            List<Map<String, Object>> result = healthcareMapper.healthInfo(map);

            if (result != null) {
                for (Map<String, Object> data : result) {
                    if (data.get("spo2Enc") != null) {
                        data.put("spo2", HealthcareEncryptionUtil.decrypt(
                            (String) data.get("spo2Enc"),
                            KeyType.HEALTH
                        ));
                    }
                    if (data.get("heartrateEnc") != null) {
                        data.put("heartrate", HealthcareEncryptionUtil.decrypt(
                            (String) data.get("heartrateEnc"),
                            KeyType.HEALTH
                        ));
                    }
                    if (data.get("bloodpressEnc") != null) {
                        data.put("bloodpress", HealthcareEncryptionUtil.decrypt(
                            (String) data.get("bloodpressEnc"),
                            KeyType.HEALTH
                        ));
                    }
                }
            }

            log.info("건강 정보 조회 완료: 사용자 {}, 결과 크기: {}", userId, result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            sample.stop(healthcareMetrics.getHealthInfoQueryTime());
            MDC.clear();
        }
    }

    @Cacheable(value = "healthChart", key = "#map['userId'] + '_' + #map['date'] + '_' + #map['query'] + '_minmax'")
    public Map<String, Object> minmaxHealthInfoChart(Map<String, Object> map) {
        return healthcareMapper.minmaxHealthInfoChart(map);
    }

    @Cacheable(value = "healthChart", key = "#map['userId'] + '_' + #map['date'] + '_' + #map['query']")
    public Map<String, Object> healthInfoChart(Map<String, Object> map) {
        return healthcareMapper.healthInfoChart(map);
    }

    @Transactional
    public int insHealthInfoTest(TestDto dto) {
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
            throw new IllegalStateException("사용자 ID에 대한 사용자 시퀀스를 찾을 수 없습니다: " + dto.getUserId());
        }
        return healthcareMapper.insHealthInfoTest(dto);
    }

    public int testInsertMinute(TestDto dto) {
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
            throw new IllegalStateException("사용자 ID에 대한 사용자 시퀀스를 찾을 수 없습니다: " + dto.getUserId());
        }
        return healthcareMapper.testInsertMinute(dto);
    }

    @Cacheable(value = "healthChart", key = "#map['userId'] + '_' + #map['date'] + '_customMinute'")
    public Map<String, Object> customMinuteChartData(Map<String, Object> map) {
        return healthcareMapper.customMinuteChartData(map);
    }

    @Cacheable(value = "healthChart", key = "#map['userId'] + '_' + #map['date'] + '_dashBRD'")
    public Map<String, Object> customMinuteDashBRDChart(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        result.put("half", healthcareMapper.halfDashBRDChart(map));
        result.put("min", healthcareMapper.fiveMinuteDashBRDChart(map));
        result.put("hour", healthcareMapper.hourDashBRDChart(map));
        return result;
    }

    @Cacheable(value = "healthData", key = "#map['userId'] + '_' + #map['date'] + '_sleep'")
    public Map<String, Object> todaySleepdata(Map<String, Object> map) {
        return healthcareMapper.todaySleepdata(map);
    }

    @Cacheable(value = "healthData", key = "#map['userId'] + '_realtime'")
    public Map<String, Object> realtimeBiodata(Map<String, Object> map) {
        return healthcareMapper.realtimeBiodata(map);
    }

    @Override
    @Cacheable(value = "healthData", key = "#map['userId'] + '_' + #map['date'] + '_graph'")
    public Map<String, Object> graphBiodata(Map<String, Object> map) {
        return healthcareMapper.graphBiodata(map);
    }

    @Override
    @Cacheable(value = "healthData", key = "#map['userId'] + '_' + #map['date'] + '_dailySleep'")
    public Map<String, Object> healthinfoDailySleep(Map<String, Object> map) {
        return healthcareMapper.healthinfoDailySleep(map);
    }

    @Transactional
    @CacheEvict(value = { "healthData", "healthInfo" }, key = "#map['userId'] + '_' + #map['date']")
    public int insertDailyStep(Map<String, Object> map) {
        int result = 0;
        for (Map<String, Object> obj : (List<Map<String, Object>>) map.get("data")) {
            obj.forEach((key, value) -> {
                if (value.getClass() == ArrayList.class)
                    obj.put(key, value.toString());
            });
            map.forEach((key, value) -> {
                if (!"data".equals(key))
                    obj.put(key, value);
            });
            result += healthcareMapper.insertDailyStep(obj);
        }
        return result;
    }

    @Transactional
    @CacheEvict(value = { "healthData", "healthInfo" }, key = "#map['userId'] + '_' + #map['date']")
    public int insertDailySleep(Map<String, Object> map) {
        int result = 0;
        for (Map<String, Object> obj : (List<Map<String, Object>>) map.get("data")) {
            obj.forEach((key, value) -> {
                if (value.getClass() == ArrayList.class)
                    obj.put(key, value.toString());
            });
            map.forEach((key, value) -> {
                if (!"data".equals(key))
                    obj.put(key, value);
            });
            result += healthcareMapper.insertDailySleep(obj);
        }
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#map['userId'] + '_' + #map['date']")
    public int insSleepScore(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) map.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insSleepScore");

        try {
            log.info("수면 점수 계산 중: {}", userId);

            int score = 0, result = 0;
            score = healthcareMapper.getSleepScore(map);

            log.info("수면 점수 계산 완료: {}, 사용자: {}", score, userId);

            if (map.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) map.get("userSeq"));
            scoreDto.setScoreField("sleep");
            scoreDto.setUserScore(score);
            scoreDto.setDate((String) map.get("date"));

            result = healthcareMapper.insScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("수면 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return result;
        } catch (Exception e) {
            log.error("수면 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#map['userId'] + '_' + #map['date']")
    public int insExerciseScore(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) map.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insExerciseScore");

        try {
            log.info("운동 점수 계산 중: {}", userId);

            double personalScore = 0.0;
            double score = 0;
            int result = 0;
            personalScore = healthcareMapper.weeklyPersonalExerciseScore(map);
            score = healthcareMapper.criteriaToCalculate(userId);

            double finalScore = (personalScore / (score * 7)) * 100;

            log.info("개인 운동 점수: {}, 연령별 기준 점수: {}, 최종 운동 점수: {}, 사용자: {}",
                    personalScore, score, finalScore, userId);

            if (map.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) map.get("userSeq"));
            scoreDto.setScoreField("exercise");
            scoreDto.setUserScore(finalScore);
            scoreDto.setDate((String) map.get("date"));

            result = healthcareMapper.insScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("운동 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return result;
        } catch (Exception e) {
            log.error("운동 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "healthScore", key = "#map['userId'] + '_' + #map['date']")
    public int insStressScore(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) map.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "insStressScore");

        try {
            log.info("스트레스 점수 계산 중: {}", userId);

            int score, result = 0;
            score = healthcareMapper.StressScore(userId);

            log.info("스트레스 점수 계산 완료: {}, 사용자: {}", score, userId);

            if (map.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setUserId(userId);
            scoreDto.setUserSeq((Integer) map.get("userSeq"));
            scoreDto.setScoreField("stress");
            scoreDto.setUserScore(score);
            scoreDto.setDate((String) map.get("date"));

            result = healthcareMapper.insScore(scoreDto);

            if (result > 0) {
                healthcareMetrics.incrementHealthScoreCalculated();
            }

            log.info("스트레스 점수 저장 완료: 사용자 {}, 결과: {}", userId, result);

            return result;
        } catch (Exception e) {
            log.error("스트레스 점수 계산 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "healthScore", key = "#map['userId'] + '_' + #map['date']")
    public Map<String, Object> healthScoreList(Map<String, Object> map) {
        Map<String, Object> result = healthcareMapper.healthScoreList(map);
        result.putAll(healthcareMapper.infoHealthScore((String) map.get("userId")));
        return result;
    }

    @Override
    @Cacheable(value = "healthScore", key = "#userId")
    public Map<String, Object> infoHealthScore(String userId) {
        return healthcareMapper.infoHealthScore(userId);
    }

    @Override
    @Cacheable(value = "healthData", key = "#dto.userId + '_target'")
    public Map<String, Object> getTarget(TargetDto dto) {
        return healthcareMapper.getTarget(dto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "community", allEntries = true)
    public int inscommunity(Map<String, Object> map) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) map.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "inscommunity");

        try {
            log.info("커뮤니티 게시글 생성 중: {}", userId);

            if (map.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            int result = healthcareMapper.inscommunity(map);

            if (result > 0) {
                healthcareMetrics.incrementCommunityPostCreated();
            }

            log.info("커뮤니티 게시글 생성 완료: 사용자 {}, 결과: {}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("커뮤니티 게시글 생성 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "community", key = "#map['pageIdx'] + '_' + (#map['searchKeyword'] != null ? #map['searchKeyword'] : '')")
    public List<Map<String, Object>> commuList(Map<String, Object> map) {
        return healthcareMapper.commulist(map);
    }

    @Override
    @Cacheable(value = "healthData", key = "#map['userId'] + '_ai_' + #map['date']")
    public Map<String, Object> getAiResponse(Map<String, Object> map) {
        if (map.get("userSeq") == null) {
            String userId = (String) map.get("userId");
            Map<String, String> request = new HashMap<>();
            request.put("userId", userId);
            Map<String, Object> response = authServiceClient.getUserSeq(request);
            if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("userSeq") != null) {
                    map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                }
            }
        }
        return healthcareMapper.getAiResponse(map);
    }

    @Override
    public int insAiResponse(Map<String, Object> map) {
        if (map.get("userSeq") == null) {
            String userId = (String) map.get("userId");
            Map<String, String> request = new HashMap<>();
            request.put("userId", userId);
            Map<String, Object> response = authServiceClient.getUserSeq(request);
            if (response != null && response.get("resultCode") != null && "0000".equals(response.get("resultCode"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("userSeq") != null) {
                    map.put("userSeq", ((Number) data.get("userSeq")).intValue());
                }
            }
        }
        return healthcareMapper.insAiResponse(map);
    }

}
