package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthcareServiceImpl implements HealthcareService {

    @Autowired
    HealthcareMapper healthcareMapper;

    public List<Map<String, Object>> selectList(Map<String, Object> map) {
        return healthcareMapper.selectList(map);
    }

    @Transactional
    public int insMinuteData(MinuteDataDto dto) {
        return healthcareMapper.insMinuteData(dto);
    }

    @Transactional
    public int insMonthDayData(MonthDayDataDto dto) {
        return healthcareMapper.insMonthDayData(dto);
    }

    public List<Map<String, Object>> minmaxHealthInfo(Map<String, Object> map) {
        return healthcareMapper.minmaxHealthInfo(map);
    }

    public List<Map<String, Object>> healthInfo(Map<String, Object> map) {
        return healthcareMapper.healthInfo(map);
    }

    public Map<String, Object> minmaxHealthInfoChart(Map<String, Object> map) {
        return healthcareMapper.minmaxHealthInfoChart(map);
    }

    public Map<String, Object> healthInfoChart(Map<String, Object> map) {
        return healthcareMapper.healthInfoChart(map);
    }

    @Transactional
    public int insHealthInfoTest(TestDto dto) {
        return healthcareMapper.insHealthInfoTest(dto);
    }

    public int testInsertMinute(TestDto dto) {
        return healthcareMapper.testInsertMinute(dto);
    }

    public Map<String, Object> customMinuteChartData(Map<String, Object> map) {
        return healthcareMapper.customMinuteChartData(map);
    }

    public Map<String, Object> customMinuteDashBRDChart(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        result.put("half", healthcareMapper.halfDashBRDChart(map));
        result.put("min", healthcareMapper.fiveMinuteDashBRDChart(map));
        result.put("hour", healthcareMapper.hourDashBRDChart(map));
        return result;
    }

    public Map<String, Object> todaySleepdata(Map<String, Object> map) {
        return healthcareMapper.todaySleepdata(map);
    }

    public Map<String, Object> realtimeBiodata(Map<String, Object> map) {
        return healthcareMapper.realtimeBiodata(map);
    }

    @Override
    public Map<String, Object> graphBiodata(Map<String, Object> map) {
        return healthcareMapper.graphBiodata(map);
    }

    @Override
    public Map<String, Object> healthinfoDailySleep(Map<String, Object> map) {
        return healthcareMapper.healthinfoDailySleep(map);
    }

    @Transactional
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
    public int insSleepScore(Map<String, Object> map) {
        int score = 0, result = 0;
        Map<String, Object> resultData = new HashMap<>();
        score = healthcareMapper.getSleepScore(map);

        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setUserId((String) map.get("userId"));
        scoreDto.setScoreField("sleep");
        scoreDto.setUserScore(score);
        scoreDto.setDate((String) map.get("date"));

        result = healthcareMapper.insScore(scoreDto);
        return result;
    }

    @Override
    public int insExerciseScore(Map<String, Object> map) {
        double personalScore = 0.0;
        double score = 0;
        int cal, result = 0;
        personalScore = healthcareMapper.weeklyPersonalExerciseScore(map);
        score = healthcareMapper.criteriaToCalculate((String) map.get("userId"));

        log.info("ash personal score: " + personalScore);
        log.info("ash 연령별 score: " + score);
        log.info("ash 총 점수 : " + (personalScore / (score * 7)) * 100);

        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setUserId((String) map.get("userId"));
        scoreDto.setScoreField("exercise");
        scoreDto.setUserScore((personalScore / (score * 7)) * 100);
        scoreDto.setDate((String) map.get("date"));

        result = healthcareMapper.insScore(scoreDto);

        return result;
    }

    @Override
    public int insStressScore(Map<String, Object> map) {
        int score, result = 0;
        score = healthcareMapper.StressScore((String) map.get("userId"));
        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setUserId((String) map.get("userId"));
        scoreDto.setScoreField("stress");
        scoreDto.setUserScore(score);
        scoreDto.setDate((String) map.get("date"));

        result = healthcareMapper.insScore(scoreDto);
        return 0;
    }

    @Override
    public Map<String, Object> healthScoreList(Map<String, Object> map) {
        Map<String, Object> result = healthcareMapper.healthScoreList(map);
        result.putAll(healthcareMapper.infoHealthScore((String) map.get("userId")));
        return result;
    }

    @Override
    public Map<String, Object> infoHealthScore(String userId) {
        return healthcareMapper.infoHealthScore(userId);
    }

    @Override
    public Map<String, Object> getTarget(TargetDto dto) {
        return healthcareMapper.getTarget(dto);
    }

    @Override
    public int inscommunity(Map<String, Object> map) {
        return healthcareMapper.inscommunity(map);
    }

    @Override
    public List<Map<String, Object>> commuList(Map<String, Object> map) {
        return healthcareMapper.commulist(map);
    }

    @Override
    public Map<String, Object> getAiResponse(Map<String, Object> map) {
        return healthcareMapper.getAiResponse(map);
    }

    @Override
    public int insAiResponse(Map<String, Object> map) {
        return healthcareMapper.insAiResponse(map);
    }

}
