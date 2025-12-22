package com.sleekydz86.service.healthcare.service.chart;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.repository.ChartDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChartDataService 단위 테스트")
class ChartDataServiceTest {

    @Mock
    private ChartDataRepository chartDataRepository;

    @InjectMocks
    private ChartDataServiceImpl chartDataService;

    private Map<String, Object> params;

    @BeforeEach
    void setUp() {
        params = new HashMap<>();
        params.put("userId", "testUser");
        params.put("date", "2025-01-01");
        params.put("query", "D");
    }

    @Test
    @DisplayName("건강 정보 차트 조회 성공")
    void getHealthInfoChart_Success() {
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("lv", new String[]{"1", "2", "3"});
        expectedResult.put("data", new int[]{100, 120, 110});

        when(chartDataRepository.findHealthInfoChart(any())).thenReturn(expectedResult);

        ServiceResponse<Map<String, Object>> response = chartDataService.getHealthInfoChart(params);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).containsKey("lv");
        verify(chartDataRepository, times(1)).findHealthInfoChart(any());
    }

    @Test
    @DisplayName("최소/최대 건강 정보 차트 조회 성공")
    void getMinMaxHealthInfoChart_Success() {
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("data_min", new int[]{90, 100, 95});
        expectedResult.put("data_max", new int[]{110, 120, 115});

        when(chartDataRepository.findMinMaxHealthInfoChart(any())).thenReturn(expectedResult);

        ServiceResponse<Map<String, Object>> response = chartDataService.getMinMaxHealthInfoChart(params);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
        verify(chartDataRepository, times(1)).findMinMaxHealthInfoChart(any());
    }

    @Test
    @DisplayName("커스텀 분 단위 차트 데이터 조회 성공")
    void getCustomMinuteChartData_Success() {
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("data", new int[]{100, 105, 110});

        when(chartDataRepository.findCustomMinuteChartData(any())).thenReturn(expectedResult);

        ServiceResponse<Map<String, Object>> response = chartDataService.getCustomMinuteChartData(params);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
        verify(chartDataRepository, times(1)).findCustomMinuteChartData(any());
    }

    @Test
    @DisplayName("대시보드 차트 데이터 조회 성공")
    void getCustomMinuteDashBRDChart_Success() {
        Map<String, Object> halfResult = new HashMap<>();
        Map<String, Object> minResult = new HashMap<>();
        Map<String, Object> hourResult = new HashMap<>();

        when(chartDataRepository.findHalfDashBRDChart(any())).thenReturn(halfResult);
        when(chartDataRepository.findFiveMinuteDashBRDChart(any())).thenReturn(minResult);
        when(chartDataRepository.findHourDashBRDChart(any())).thenReturn(hourResult);

        ServiceResponse<Map<String, Object>> response = chartDataService.getCustomMinuteDashBRDChart(params);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).containsKey("half");
        assertThat(response.getData()).containsKey("min");
        assertThat(response.getData()).containsKey("hour");
        verify(chartDataRepository, times(1)).findHalfDashBRDChart(any());
        verify(chartDataRepository, times(1)).findFiveMinuteDashBRDChart(any());
        verify(chartDataRepository, times(1)).findHourDashBRDChart(any());
    }

    @Test
    @DisplayName("일일 수면 정보 조회 성공")
    void getHealthinfoDailySleep_Success() {
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("sleep", 480);

        when(chartDataRepository.findHealthinfoDailySleep(any())).thenReturn(expectedResult);

        ServiceResponse<Map<String, Object>> response = chartDataService.getHealthinfoDailySleep(params);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
        verify(chartDataRepository, times(1)).findHealthinfoDailySleep(any());
    }

    @Test
    @DisplayName("차트 데이터 조회 실패 - 예외 발생")
    void getHealthInfoChart_Exception() {
        when(chartDataRepository.findHealthInfoChart(any())).thenThrow(new RuntimeException("Database error"));

        ServiceResponse<Map<String, Object>> response = chartDataService.getHealthInfoChart(params);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("차트 데이터 조회 실패");
        verify(chartDataRepository, times(1)).findHealthInfoChart(any());
    }
}


