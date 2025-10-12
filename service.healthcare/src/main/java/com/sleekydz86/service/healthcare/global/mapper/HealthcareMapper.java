package com.sleekydz86.service.healthcare.global.mapper;

import com.sleekydz86.service.healthcare.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface HealthcareMapper {

    List<Map<String, Object>> selectList(Map<String, Object> map);

    List<Map<String, Object>> minmaxHealthInfo(Map<String, Object> map);

    List<Map<String, Object>> healthInfo(Map<String, Object> map);

    Map<String, Object> minmaxHealthInfoChart(Map<String, Object> map);

    Map<String, Object> healthInfoChart(Map<String, Object> map);

    int insHealthInfoTest(TestDto dto);

    int insMinuteData(MinuteDataDto dto);

    int insMonthDayData(MonthDayDataDto dto);

    Map<String, Object> healthInfoChartDay(Map<String, Object> map);

    Map<String, Object> minmaxHealthInfoChartDay(Map<String, Object> map);

    int testInsertMinute(TestDto dto);

    Map<String, Object> customMinuteChartData(Map<String, Object> map);

    Map<String, Object> customMinuteDashBRDChart(Map<String, Object> map);

    Map<String, Object> halfDashBRDChart(Map<String, Object> map);

    Map<String, Object> fiveMinuteDashBRDChart(Map<String, Object> map);

    Map<String, Object> hourDashBRDChart(Map<String, Object> map);

    Map<String,Object> todaySleepdata(Map<String, Object> map);
    Map<String, Object> realtimeBiodata(Map<String, Object> map);

    Map<String, Object> graphBiodata(Map<String, Object> map);

    Map<String, Object> healthinfoDailySleep(Map<String, Object> map);

    int insertDailyStep(Map<String, Object> map);

    int insertDailySleep(Map<String, Object> map);

    int getSleepScore(Map<String, Object> map);

    int insScore(ScoreDto scoreDto);

    double criteriaToCalculate(String userId);

    double weeklyPersonalExerciseScore(Map<String, Object> map);

    int StressScore(String userId);

    Map<String, Object> infoHealthScore(String UserId);
    Map<String, Object> healthScoreList(Map<String, Object> map);

    List<Map<String,Object>> birthlisttest();
    int ageinsert(Map<String,Object> map);

    public Map<String, Object> getTarget(TargetDto dto);

    public int inscommunity(Map<String,Object> map);
    public List<Map<String,Object>> commulist(Map<String,Object> map);
    public Map<String,Object> getAiResponse(Map<String,Object> map);
    public int insAiResponse(Map<String, Object> map);

}
