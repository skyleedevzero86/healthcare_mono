package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;

import java.util.List;
import java.util.Map;

public class HealthDataRepositoryImpl implements HealthDataRepository {
    private final HealthcareMapper healthcareMapper;

    public HealthDataRepositoryImpl(HealthcareMapper healthcareMapper) {
        this.healthcareMapper = healthcareMapper;
    }

    @Override
    public int insertMinuteData(MinuteDataDto dto) {
        return healthcareMapper.insMinuteData(dto);
    }

    @Override
    public int insertMonthDayData(MonthDayDataDto dto) {
        return healthcareMapper.insMonthDayData(dto);
    }

    @Override
    public List<Map<String, Object>> findHealthInfo(Map<String, Object> params) {
        return healthcareMapper.healthInfo(params);
    }

    @Override
    public List<Map<String, Object>> findMinMaxHealthInfo(Map<String, Object> params) {
        return healthcareMapper.minmaxHealthInfo(params);
    }

    @Override
    public int insertHealthInfoTest(TestDto dto) {
        return healthcareMapper.insHealthInfoTest(dto);
    }

    @Override
    public int testInsertMinute(TestDto dto) {
        return healthcareMapper.testInsertMinute(dto);
    }

    @Override
    public int insertDailyStep(Map<String, Object> map) {
        return healthcareMapper.insertDailyStep(map);
    }

    @Override
    public int insertDailySleep(Map<String, Object> map) {
        return healthcareMapper.insertDailySleep(map);
    }

    @Override
    public List<Map<String, Object>> selectList(Map<String, Object> map) {
        return healthcareMapper.selectList(map);
    }
}

