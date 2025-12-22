package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.dto.*;

import java.util.List;
import java.util.Map;

public interface HealthDataRepository {
    int insertMinuteData(MinuteDataDto dto);
    int insertMonthDayData(MonthDayDataDto dto);
    List<Map<String, Object>> findHealthInfo(Map<String, Object> params);
    List<Map<String, Object>> findMinMaxHealthInfo(Map<String, Object> params);
    int insertHealthInfoTest(TestDto dto);
    int testInsertMinute(TestDto dto);
    int insertDailyStep(Map<String, Object> map);
    int insertDailySleep(Map<String, Object> map);
    List<Map<String, Object>> selectList(Map<String, Object> map);
}

