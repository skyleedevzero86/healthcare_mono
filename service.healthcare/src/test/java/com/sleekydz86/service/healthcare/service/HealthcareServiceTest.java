package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.dto.MinuteDataDto;
import com.sleekydz86.service.healthcare.dto.MonthDayDataDto;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthcareService 단위 테스트")
class HealthcareServiceTest {

    @Mock
    private HealthcareMapper healthcareMapper;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private EventStore eventStore;

    @InjectMocks
    private HealthcareServiceImpl healthcareService;

    private MinuteDataDto minuteDataDto;
    private MonthDayDataDto monthDayDataDto;
    private Map<String, Object> testMap;

    @BeforeEach
    void setUp() {
        String uniqueUserId = "testUser_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        minuteDataDto = new MinuteDataDto();
        minuteDataDto.setUserId(uniqueUserId);
        minuteDataDto.setUserSeq(1);
        minuteDataDto.setTid("T001_" + System.currentTimeMillis());
        minuteDataDto.setTime("202501011200");
        minuteDataDto.setHeartrate(72);
        minuteDataDto.setTemper(36.5f);
        minuteDataDto.setSpo2(98);
        minuteDataDto.setStep(5000);
        minuteDataDto.setStress(50);
        minuteDataDto.setBloodpressMin(80);
        minuteDataDto.setBloodpressMax(120);
        minuteDataDto.setRepiratory(16);

        monthDayDataDto = new MonthDayDataDto();
        monthDayDataDto.setUserId(uniqueUserId);
        monthDayDataDto.setUserSeq(1);
        monthDayDataDto.setTime("20250101");
        monthDayDataDto.setHeartrateMin(60);
        monthDayDataDto.setHeartrateMax(100);
        monthDayDataDto.setHeartrateAvg(75);
        monthDayDataDto.setTemperatureMin(36.0f);
        monthDayDataDto.setTemperatureMax(37.0f);
        monthDayDataDto.setTemperatureAvg(36.5f);
        monthDayDataDto.setSpo2Min(95);
        monthDayDataDto.setSpo2Max(100);
        monthDayDataDto.setSpo2Avg(98);
        monthDayDataDto.setStep(10000);
        monthDayDataDto.setSleep(480);
        monthDayDataDto.setStressMin(30);
        monthDayDataDto.setStressMax(70);
        monthDayDataDto.setStressAvg(50);
        monthDayDataDto.setBloodpressMin(70);
        monthDayDataDto.setBloodpressMax(130);
        monthDayDataDto.setRepiratoryMin(14);
        monthDayDataDto.setRepiratoryMax(18);
        monthDayDataDto.setRepiratoryAvg(16);

        testMap = new HashMap<>();
        testMap.put("userId", "testUser");
        testMap.put("date", "2025-01-01");
        testMap.put("query", "D");
    }

    @Test
    @DisplayName("분 단위 헬스 데이터 저장 성공")
    void insMinuteData_Success() {
        when(healthcareMapper.insMinuteData(any(MinuteDataDto.class))).thenReturn(1);

        int result = healthcareService.insMinuteData(minuteDataDto);

        assertThat(result).isEqualTo(1);
        verify(healthcareMapper, times(1)).insMinuteData(minuteDataDto);
        verify(eventStore, times(1)).saveEvent(any());
        verify(eventPublisher, times(1)).publishHealthDataEvent(any());
    }

    @Test
    @DisplayName("분 단위 헬스 데이터 저장 - userSeq가 null인 경우 AuthServiceClient 호출")
    void insMinuteData_WithNullUserSeq() {
        minuteDataDto.setUserSeq(null);
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("resultCode", "0000");
        Map<String, Object> data = new HashMap<>();
        data.put("userSeq", 1);
        authResponse.put("data", data);

        when(authServiceClient.getUserSeq(anyMap())).thenReturn(authResponse);
        when(healthcareMapper.insMinuteData(any(MinuteDataDto.class))).thenReturn(1);

        int result = healthcareService.insMinuteData(minuteDataDto);

        assertThat(result).isEqualTo(1);
        assertThat(minuteDataDto.getUserSeq()).isEqualTo(1);
        verify(authServiceClient, times(1)).getUserSeq(anyMap());
        verify(healthcareMapper, times(1)).insMinuteData(minuteDataDto);
    }

    @Test
    @DisplayName("분 단위 헬스 데이터 저장 - userSeq를 찾을 수 없는 경우 예외 발생")
    void insMinuteData_UserSeqNotFound() {
        minuteDataDto.setUserSeq(null);
        when(authServiceClient.getUserSeq(anyMap())).thenReturn(null);

        assertThatThrownBy(() -> healthcareService.insMinuteData(minuteDataDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("UserSeq not found");

        verify(healthcareMapper, never()).insMinuteData(any());
    }

    @Test
    @DisplayName("일 단위 헬스 데이터 저장 성공")
    void insMonthDayData_Success() {
        when(healthcareMapper.insMonthDayData(any(MonthDayDataDto.class))).thenReturn(1);

        int result = healthcareService.insMonthDayData(monthDayDataDto);

        assertThat(result).isEqualTo(1);
        verify(healthcareMapper, times(1)).insMonthDayData(monthDayDataDto);
        verify(eventStore, times(1)).saveEvent(any());
        verify(eventPublisher, times(1)).publishHealthDataEvent(any());
    }

    @Test
    @DisplayName("헬스 정보 최소/최대값 조회 성공")
    void minmaxHealthInfo_Success() {
        List<Map<String, Object>> expectedData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("heartrate_MIN", 60);
        data.put("heartrate_MAX", 100);
        expectedData.add(data);

        when(healthcareMapper.minmaxHealthInfo(anyMap())).thenReturn(expectedData);

        List<Map<String, Object>> result = healthcareService.minmaxHealthInfo(testMap);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(healthcareMapper, times(1)).minmaxHealthInfo(testMap);
    }

    @Test
    @DisplayName("헬스 정보 조회 성공")
    void healthInfo_Success() {
        List<Map<String, Object>> expectedData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("heartrate", 72);
        expectedData.add(data);

        when(healthcareMapper.healthInfo(anyMap())).thenReturn(expectedData);

        List<Map<String, Object>> result = healthcareService.healthInfo(testMap);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(healthcareMapper, times(1)).healthInfo(testMap);
    }

    @Test
    @DisplayName("헬스 차트 최소/최대값 조회 성공")
    void minmaxHealthInfoChart_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("min", 60);
        expectedData.put("max", 100);

        when(healthcareMapper.minmaxHealthInfoChart(anyMap())).thenReturn(expectedData);

        Map<String, Object> result = healthcareService.minmaxHealthInfoChart(testMap);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("min");
        assertThat(result).containsKey("max");
        verify(healthcareMapper, times(1)).minmaxHealthInfoChart(testMap);
    }

    @Test
    @DisplayName("헬스 차트 조회 성공")
    void healthInfoChart_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("data", new ArrayList<>());

        when(healthcareMapper.healthInfoChart(anyMap())).thenReturn(expectedData);

        Map<String, Object> result = healthcareService.healthInfoChart(testMap);

        assertThat(result).isNotNull();
        verify(healthcareMapper, times(1)).healthInfoChart(testMap);
    }

    @Test
    @DisplayName("실시간 생체 데이터 조회 성공")
    void realtimeBiodata_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("heartrate", 72);
        expectedData.put("temperature", 36.5);

        when(healthcareMapper.realtimeBiodata(anyMap())).thenReturn(expectedData);

        Map<String, Object> result = healthcareService.realtimeBiodata(testMap);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("heartrate");
        verify(healthcareMapper, times(1)).realtimeBiodata(testMap);
    }

    @Test
    @DisplayName("헬스 스코어 리스트 조회 성공")
    void healthScoreList_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("sleep", 85);
        expectedData.put("exercise", 90);
        expectedData.put("stress", 75);

        Map<String, Object> infoData = new HashMap<>();
        infoData.put("avgSleep", 8);
        infoData.put("avgExercise", 30);

        when(healthcareMapper.healthScoreList(anyMap())).thenReturn(expectedData);
        when(healthcareMapper.infoHealthScore(anyString())).thenReturn(infoData);

        Map<String, Object> result = healthcareService.healthScoreList(testMap);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("sleep");
        assertThat(result).containsKey("avgSleep");
        verify(healthcareMapper, times(1)).healthScoreList(testMap);
        verify(healthcareMapper, times(1)).infoHealthScore("testUser");
    }

    @Test
    @DisplayName("커뮤니티 리스트 조회 성공")
    void commuList_Success() {
        List<Map<String, Object>> expectedData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("commuSeq", 1);
        data.put("content", "테스트 게시글");
        expectedData.add(data);

        when(healthcareMapper.commulist(anyMap())).thenReturn(expectedData);

        List<Map<String, Object>> result = healthcareService.commuList(testMap);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(healthcareMapper, times(1)).commulist(testMap);
    }
}

