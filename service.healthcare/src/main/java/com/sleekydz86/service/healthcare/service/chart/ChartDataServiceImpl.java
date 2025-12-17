package com.sleekydz86.service.healthcare.service.chart;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.repository.ChartDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ChartDataServiceImpl implements ChartDataService {
    private final ChartDataRepository chartDataRepository;

    @Override
    @Cacheable(value = "healthChart", key = "#params['userId'] + '_' + #params['date'] + '_' + #params['query']")
    public ServiceResponse<Map<String, Object>> getHealthInfoChart(Map<String, Object> params) {
        try {
            if (params.containsKey("searchWrd") && params.get("searchWrd") != null) {
                String searchWrd = params.get("searchWrd").toString();
                params.put("searchWrd", com.sleekydz86.service.healthcare.util.SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            if (params.containsKey("condition") && params.get("condition") != null) {
                String condition = params.get("condition").toString();
                params.put("condition", com.sleekydz86.service.healthcare.util.SqlInjectionValidator.sanitizeCondition(condition));
            }
            Map<String, Object> result = chartDataRepository.findHealthInfoChart(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("차트 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("차트 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthChart", key = "#params['userId'] + '_' + #params['date'] + '_' + #params['query'] + '_minmax'")
    public ServiceResponse<Map<String, Object>> getMinMaxHealthInfoChart(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findMinMaxHealthInfoChart(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("최소/최대 차트 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("차트 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthChart", key = "#params['userId'] + '_' + #params['date'] + '_customMinute'")
    public ServiceResponse<Map<String, Object>> getCustomMinuteChartData(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findCustomMinuteChartData(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("커스텀 분 단위 차트 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("차트 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthChart", key = "#params['userId'] + '_' + #params['date'] + '_dashBRD'")
    public ServiceResponse<Map<String, Object>> getCustomMinuteDashBRDChart(Map<String, Object> params) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("half", chartDataRepository.findHalfDashBRDChart(params));
            result.put("min", chartDataRepository.findFiveMinuteDashBRDChart(params));
            result.put("hour", chartDataRepository.findHourDashBRDChart(params));
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("대시보드 차트 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("차트 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthData", key = "#params['userId'] + '_' + #params['date'] + '_sleep'")
    public ServiceResponse<Map<String, Object>> getTodaySleepData(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findTodaySleepdata(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("오늘 수면 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("수면 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthData", key = "#params['userId'] + '_realtime'")
    public ServiceResponse<Map<String, Object>> getRealtimeBiodata(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findRealtimeBiodata(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("실시간 생체 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("실시간 생체 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthData", key = "#params['userId'] + '_' + #params['date'] + '_graph'")
    public ServiceResponse<Map<String, Object>> getGraphBiodata(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findGraphBiodata(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("그래프 생체 데이터 조회 중 오류 발생", e);
            return ServiceResponse.error("그래프 생체 데이터 조회 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "healthData", key = "#params['userId'] + '_' + #params['date'] + '_dailySleep'")
    public ServiceResponse<Map<String, Object>> getHealthinfoDailySleep(Map<String, Object> params) {
        try {
            Map<String, Object> result = chartDataRepository.findHealthinfoDailySleep(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("일일 수면 정보 조회 중 오류 발생", e);
            return ServiceResponse.error("일일 수면 정보 조회 실패: " + e.getMessage());
        }
    }
}

