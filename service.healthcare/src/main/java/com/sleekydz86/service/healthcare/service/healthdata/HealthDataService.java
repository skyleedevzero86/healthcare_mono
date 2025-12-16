package com.sleekydz86.service.healthcare.service.healthdata;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.dto.*;

import java.util.List;
import java.util.Map;

public interface HealthDataService {
    ServiceResponse<Integer> insertMinuteData(MinuteDataDto dto);
    ServiceResponse<Integer> insertMonthDayData(MonthDayDataDto dto);
    ServiceResponse<List<HealthData>> getHealthInfo(Map<String, Object> params);
    ServiceResponse<List<HealthData>> getMinMaxHealthInfo(Map<String, Object> params);
    ServiceResponse<Integer> insertHealthInfoTest(TestDto dto);
    ServiceResponse<Integer> testInsertMinute(TestDto dto);
    ServiceResponse<Integer> insertDailyStep(Map<String, Object> params);
    ServiceResponse<Integer> insertDailySleep(Map<String, Object> params);
}

