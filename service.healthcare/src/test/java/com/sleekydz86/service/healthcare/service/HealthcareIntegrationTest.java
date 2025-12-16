package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.dto.MinuteDataDto;
import com.sleekydz86.service.healthcare.dto.MonthDayDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("HealthcareService 통합 테스트")
class HealthcareIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("healthcare_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HealthcareService healthcareService;

    private Map<String, Object> testMap;

    @BeforeEach
    void setUp() {
        testMap = new HashMap<>();
        testMap.put("userId", "testUser");
        testMap.put("date", "2025-01-01");
        testMap.put("query", "D");
    }

    @Test
    @DisplayName("헬스 데이터 플로우 통합 테스트")
    void testHealthDataFlow() {
        MinuteDataDto minuteDataDto = new MinuteDataDto();
        minuteDataDto.setUserId("testUser");
        minuteDataDto.setUserSeq(1);
        minuteDataDto.setTid("T001");
        minuteDataDto.setTime("202501011200");
        minuteDataDto.setHeartrate(72);
        minuteDataDto.setTemper(36.5f);
        minuteDataDto.setSpo2(98);
        minuteDataDto.setStep(5000);
        minuteDataDto.setStress(50);
        minuteDataDto.setBloodpressMin(80);
        minuteDataDto.setBloodpressMax(120);
        minuteDataDto.setRepiratory(16);

        int insertResult = healthcareService.insMinuteData(minuteDataDto);
        assertThat(insertResult).isGreaterThan(0);

        List<Map<String, Object>> healthInfo = healthcareService.healthInfo(testMap);
        assertThat(healthInfo).isNotNull();

        Map<String, Object> chartData = healthcareService.healthInfoChart(testMap);
        assertThat(chartData).isNotNull();
    }

    @Test
    @DisplayName("일 단위 헬스 데이터 저장 및 조회 통합 테스트")
    void testMonthDayDataFlow() {
        MonthDayDataDto monthDayDataDto = new MonthDayDataDto();
        monthDayDataDto.setUserId("testUser");
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

        int insertResult = healthcareService.insMonthDayData(monthDayDataDto);
        assertThat(insertResult).isGreaterThan(0);

        List<Map<String, Object>> minmaxInfo = healthcareService.minmaxHealthInfo(testMap);
        assertThat(minmaxInfo).isNotNull();

        Map<String, Object> minmaxChart = healthcareService.minmaxHealthInfoChart(testMap);
        assertThat(minmaxChart).isNotNull();
    }

    @Test
    @DisplayName("헬스 스코어 조회 통합 테스트")
    void testHealthScoreFlow() {
        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put("userId", "testUser");
        scoreMap.put("date", "2025-01-01");

        Map<String, Object> scoreList = healthcareService.healthScoreList(scoreMap);
        assertThat(scoreList).isNotNull();

        Map<String, Object> infoScore = healthcareService.infoHealthScore("testUser");
        assertThat(infoScore).isNotNull();
    }
}

