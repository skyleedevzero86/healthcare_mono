package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.dto.*;

import java.util.List;
import java.util.Map;

public interface HealthcareService {
    public List<Map<String, Object>> selectList(Map<String, Object> map);

    public int insMinuteData(MinuteDataDto dto);

    public int insMonthDayData(MonthDayDataDto dto);

    public List<Map<String, Object>> minmaxHealthInfo(Map<String, Object> map);

    public List<Map<String, Object>> healthInfo(Map<String, Object> map);

    public Map<String, Object> minmaxHealthInfoChart(Map<String, Object> map);

    public Map<String, Object> healthInfoChart(Map<String, Object> map);

    public int insHealthInfoTest(TestDto dto);

    public int testInsertMinute(TestDto dto);

    public Map<String, Object> customMinuteChartData(Map<String, Object> map);

    public Map<String, Object> customMinuteDashBRDChart(Map<String, Object> map);

    public Map<String, Object> todaySleepdata(Map<String, Object> map);

    public Map<String, Object> realtimeBiodata(Map<String, Object> map);

    public Map<String, Object> graphBiodata(Map<String, Object> map);

    public Map<String, Object> healthinfoDailySleep(Map<String, Object> map);

    public int insertDailyStep(Map<String, Object> map);

    public int insertDailySleep(Map<String, Object> map);

    public int insSleepScore(Map<String, Object> map);

    public int insExerciseScore(Map<String, Object> map);

    public int insStressScore(Map<String, Object> map);

    public Map<String, Object> healthScoreList(Map<String, Object> map);

    public Map<String, Object> infoHealthScore(String userId);

    public Map<String, Object> getTarget(TargetDto dto);

    public int inscommunity(Map<String, Object> map);

    public List<Map<String, Object>> commuList(Map<String, Object> map);

    public Map<String, Object> getAiResponse(Map<String, Object> map);

    public int insAiResponse(Map<String, Object> map);
}
