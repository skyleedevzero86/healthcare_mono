package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

public class ChartDataRepositoryImpl implements ChartDataRepository {
    private final HealthcareMapper healthcareMapper;

    public ChartDataRepositoryImpl(HealthcareMapper healthcareMapper) {
        this.healthcareMapper = healthcareMapper;
    }

    @Override
    public Map<String, Object> findHealthInfoChart(Map<String, Object> params) {
        return healthcareMapper.healthInfoChart(params);
    }

    @Override
    public Map<String, Object> findMinMaxHealthInfoChart(Map<String, Object> params) {
        return healthcareMapper.minmaxHealthInfoChart(params);
    }

    @Override
    public Map<String, Object> findCustomMinuteChartData(Map<String, Object> params) {
        return healthcareMapper.customMinuteChartData(params);
    }

    @Override
    public Map<String, Object> findHalfDashBRDChart(Map<String, Object> params) {
        return healthcareMapper.halfDashBRDChart(params);
    }

    @Override
    public Map<String, Object> findFiveMinuteDashBRDChart(Map<String, Object> params) {
        return healthcareMapper.fiveMinuteDashBRDChart(params);
    }

    @Override
    public Map<String, Object> findHourDashBRDChart(Map<String, Object> params) {
        return healthcareMapper.hourDashBRDChart(params);
    }

    @Override
    public Map<String, Object> findTodaySleepdata(Map<String, Object> params) {
        return healthcareMapper.todaySleepdata(params);
    }

    @Override
    public Map<String, Object> findRealtimeBiodata(Map<String, Object> params) {
        return healthcareMapper.realtimeBiodata(params);
    }

    @Override
    public Map<String, Object> findGraphBiodata(Map<String, Object> params) {
        return healthcareMapper.graphBiodata(params);
    }

    @Override
    public Map<String, Object> findHealthinfoDailySleep(Map<String, Object> params) {
        return healthcareMapper.healthinfoDailySleep(params);
    }
}

