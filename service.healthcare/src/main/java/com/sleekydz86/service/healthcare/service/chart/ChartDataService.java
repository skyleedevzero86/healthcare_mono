package com.sleekydz86.service.healthcare.service.chart;

import com.sleekydz86.service.healthcare.common.ServiceResponse;

import java.util.Map;

public interface ChartDataService {
    ServiceResponse<Map<String, Object>> getHealthInfoChart(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getMinMaxHealthInfoChart(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getCustomMinuteChartData(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getCustomMinuteDashBRDChart(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getTodaySleepData(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getRealtimeBiodata(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getGraphBiodata(Map<String, Object> params);
    ServiceResponse<Map<String, Object>> getHealthinfoDailySleep(Map<String, Object> params);
}

