package com.sleekydz86.service.healthcare.repository;

import java.util.Map;

public interface ChartDataRepository {
    Map<String, Object> findHealthInfoChart(Map<String, Object> params);
    Map<String, Object> findMinMaxHealthInfoChart(Map<String, Object> params);
    Map<String, Object> findCustomMinuteChartData(Map<String, Object> params);
    Map<String, Object> findHalfDashBRDChart(Map<String, Object> params);
    Map<String, Object> findFiveMinuteDashBRDChart(Map<String, Object> params);
    Map<String, Object> findHourDashBRDChart(Map<String, Object> params);
    Map<String, Object> findTodaySleepdata(Map<String, Object> params);
    Map<String, Object> findRealtimeBiodata(Map<String, Object> params);
    Map<String, Object> findGraphBiodata(Map<String, Object> params);
    Map<String, Object> findHealthinfoDailySleep(Map<String, Object> params);
}

