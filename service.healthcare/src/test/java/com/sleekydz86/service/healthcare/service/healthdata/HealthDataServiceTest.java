package com.sleekydz86.service.healthcare.service.healthdata;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.dto.HealthData;
import com.sleekydz86.service.healthcare.dto.MinuteDataDto;
import com.sleekydz86.service.healthcare.dto.MonthDayDataDto;
import com.sleekydz86.service.healthcare.dto.TestDto;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.HealthDataRepository;
import com.sleekydz86.service.healthcare.strategy.DataProcessingService;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthDataServiceTest {

    @Mock
    private HealthDataRepository healthDataRepository;

    @Mock
    private HealthDataValidator healthDataValidator;

    @Mock
    private DataProcessingService dataProcessingService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private EventStore eventStore;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private HealthcareMetrics healthcareMetrics;

    @InjectMocks
    private HealthDataServiceImpl healthDataService;

    private MinuteDataDto minuteDataDto;
    private MonthDayDataDto monthDayDataDto;
    private TestDto testDto;
    private Map<String, Object> params;
    private Timer.Sample timerSample;

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

        testDto = new TestDto();
        testDto.setUserId(uniqueUserId);
        testDto.setUserSeq(1);

        params = new HashMap<>();
        params.put("userId", "testUser");
        params.put("date", "2025-01-01");

        timerSample = mock(Timer.Sample.class);
        when(healthcareMetrics.startHealthDataProcessingTimer()).thenReturn(timerSample);
        when(healthcareMetrics.startHealthInfoQueryTimer()).thenReturn(timerSample);
        when(healthcareMetrics.getHealthDataProcessingTime()).thenReturn(mock(Timer.class));
        when(healthcareMetrics.getHealthInfoQueryTime()).thenReturn(mock(Timer.class));
    }

    @Test
    void insertMinuteData_Success() {
        when(healthDataRepository.insertMinuteData(any(MinuteDataDto.class))).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(MinuteDataDto.class));
        doNothing().when(eventStore).saveEvent(any());
        doNothing().when(eventPublisher).publishHealthDataEvent(any());

        ServiceResponse<Integer> response = healthDataService.insertMinuteData(minuteDataDto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(minuteDataDto);
        verify(healthDataRepository).insertMinuteData(minuteDataDto);
        verify(eventStore).saveEvent(any());
        verify(eventPublisher).publishHealthDataEvent(any());
        verify(healthcareMetrics).incrementHealthDataProcessed();
        verify(healthcareMetrics).incrementHealthDataProcessedMinute();
    }

    @Test
    void insertMinuteData_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 데이터")).when(healthDataValidator).validate(any(MinuteDataDto.class));

        ServiceResponse<Integer> response = healthDataService.insertMinuteData(minuteDataDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(minuteDataDto);
        verify(healthDataRepository, never()).insertMinuteData(any(MinuteDataDto.class));
    }

    @Test
    void insertMinuteData_UserSeqNotFound() {
        minuteDataDto.setUserSeq(null);
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("resultCode", "1001");
        when(authServiceClient.getUserSeq(any())).thenReturn(authResponse);
        doNothing().when(healthDataValidator).validate(any(MinuteDataDto.class));

        ServiceResponse<Integer> response = healthDataService.insertMinuteData(minuteDataDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("사용자 시퀀스를 찾을 수 없습니다"));
        verify(authServiceClient).getUserSeq(any());
        verify(healthDataRepository, never()).insertMinuteData(any(MinuteDataDto.class));
    }

    @Test
    void insertMinuteData_UserSeqFromAuthService() {
        minuteDataDto.setUserSeq(null);
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("resultCode", "0000");
        Map<String, Object> data = new HashMap<>();
        data.put("userSeq", 1);
        authResponse.put("data", data);
        when(authServiceClient.getUserSeq(any())).thenReturn(authResponse);
        when(healthDataRepository.insertMinuteData(any(MinuteDataDto.class))).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(MinuteDataDto.class));
        doNothing().when(eventStore).saveEvent(any());
        doNothing().when(eventPublisher).publishHealthDataEvent(any());

        ServiceResponse<Integer> response = healthDataService.insertMinuteData(minuteDataDto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(authServiceClient).getUserSeq(any());
        verify(healthDataRepository).insertMinuteData(any(MinuteDataDto.class));
    }

    @Test
    void insertMinuteData_Exception() {
        doNothing().when(healthDataValidator).validate(any(MinuteDataDto.class));
        when(healthDataRepository.insertMinuteData(any(MinuteDataDto.class))).thenThrow(new RuntimeException("데이터베이스 오류"));

        ServiceResponse<Integer> response = healthDataService.insertMinuteData(minuteDataDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("데이터 삽입 실패"));
        verify(healthDataRepository).insertMinuteData(minuteDataDto);
    }

    @Test
    void insertMonthDayData_Success() {
        when(healthDataRepository.insertMonthDayData(any(MonthDayDataDto.class))).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(MonthDayDataDto.class));
        doNothing().when(eventStore).saveEvent(any());
        doNothing().when(eventPublisher).publishHealthDataEvent(any());

        ServiceResponse<Integer> response = healthDataService.insertMonthDayData(monthDayDataDto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(monthDayDataDto);
        verify(healthDataRepository).insertMonthDayData(monthDayDataDto);
    }

    @Test
    void insertMonthDayData_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 데이터")).when(healthDataValidator).validate(any(MonthDayDataDto.class));

        ServiceResponse<Integer> response = healthDataService.insertMonthDayData(monthDayDataDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(monthDayDataDto);
        verify(healthDataRepository, never()).insertMonthDayData(any(MonthDayDataDto.class));
    }

    @Test
    void getHealthInfo_Success() {
        List<Map<String, Object>> rawResult = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("userId", "testUser");
        data1.put("date", "2025-01-01");
        rawResult.add(data1);
        when(healthDataRepository.findHealthInfo(any())).thenReturn(rawResult);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<List<HealthData>> response = healthDataService.getHealthInfo(params);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository).findHealthInfo(params);
    }

    @Test
    void getHealthInfo_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 파라미터")).when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<List<HealthData>> response = healthDataService.getHealthInfo(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).findHealthInfo(any());
    }

    @Test
    void getHealthInfo_Exception() {
        doNothing().when(healthDataValidator).validate(any(Map.class));
        when(healthDataRepository.findHealthInfo(any())).thenThrow(new RuntimeException("데이터베이스 오류"));

        ServiceResponse<List<HealthData>> response = healthDataService.getHealthInfo(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("조회 실패"));
        verify(healthDataRepository).findHealthInfo(params);
    }

    @Test
    void getMinMaxHealthInfo_Success() {
        List<Map<String, Object>> rawResult = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("userId", "testUser");
        data1.put("date", "2025-01-01");
        rawResult.add(data1);
        when(healthDataRepository.findMinMaxHealthInfo(any())).thenReturn(rawResult);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<List<HealthData>> response = healthDataService.getMinMaxHealthInfo(params);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository).findMinMaxHealthInfo(params);
    }

    @Test
    void getMinMaxHealthInfo_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 파라미터")).when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<List<HealthData>> response = healthDataService.getMinMaxHealthInfo(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).findMinMaxHealthInfo(any());
    }

    @Test
    void insertHealthInfoTest_Success() {
        when(healthDataRepository.insertHealthInfoTest(any(TestDto.class))).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(TestDto.class));

        ServiceResponse<Integer> response = healthDataService.insertHealthInfoTest(testDto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(testDto);
        verify(healthDataRepository).insertHealthInfoTest(testDto);
    }

    @Test
    void insertHealthInfoTest_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 데이터")).when(healthDataValidator).validate(any(TestDto.class));

        ServiceResponse<Integer> response = healthDataService.insertHealthInfoTest(testDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(testDto);
        verify(healthDataRepository, never()).insertHealthInfoTest(any(TestDto.class));
    }

    @Test
    void testInsertMinute_Success() {
        when(healthDataRepository.testInsertMinute(any(TestDto.class))).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(TestDto.class));

        ServiceResponse<Integer> response = healthDataService.testInsertMinute(testDto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(testDto);
        verify(healthDataRepository).testInsertMinute(testDto);
    }

    @Test
    void testInsertMinute_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 데이터")).when(healthDataValidator).validate(any(TestDto.class));

        ServiceResponse<Integer> response = healthDataService.testInsertMinute(testDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(testDto);
        verify(healthDataRepository, never()).testInsertMinute(any(TestDto.class));
    }

    @Test
    void insertDailyStep_Success() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("step", 5000);
        dataList.add(data1);
        params.put("data", dataList);
        when(dataProcessingService.processData(any())).thenReturn(params);
        when(healthDataRepository.insertDailyStep(any())).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailyStep(params);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(params);
        verify(dataProcessingService).processData(params);
        verify(healthDataRepository).insertDailyStep(any());
    }

    @Test
    void insertDailyStep_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 파라미터")).when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailyStep(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).insertDailyStep(any());
    }

    @Test
    void insertDailyStep_EmptyDataList() {
        params.put("data", new ArrayList<>());
        when(dataProcessingService.processData(any())).thenReturn(params);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailyStep(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("데이터 목록이 비어있습니다"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).insertDailyStep(any());
    }

    @Test
    void insertDailySleep_Success() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sleep", 480);
        dataList.add(data1);
        params.put("data", dataList);
        when(dataProcessingService.processData(any())).thenReturn(params);
        when(healthDataRepository.insertDailySleep(any())).thenReturn(1);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailySleep(params);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData());
        verify(healthDataValidator).validate(params);
        verify(dataProcessingService).processData(params);
        verify(healthDataRepository).insertDailySleep(any());
    }

    @Test
    void insertDailySleep_ValidationException() {
        doThrow(new ValidationException("유효하지 않은 파라미터")).when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailySleep(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("검증 실패"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).insertDailySleep(any());
    }

    @Test
    void insertDailySleep_EmptyDataList() {
        params.put("data", new ArrayList<>());
        when(dataProcessingService.processData(any())).thenReturn(params);
        doNothing().when(healthDataValidator).validate(any(Map.class));

        ServiceResponse<Integer> response = healthDataService.insertDailySleep(params);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("데이터 목록이 비어있습니다"));
        verify(healthDataValidator).validate(params);
        verify(healthDataRepository, never()).insertDailySleep(any());
    }
}

