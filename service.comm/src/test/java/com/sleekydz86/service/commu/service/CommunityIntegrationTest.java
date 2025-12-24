package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.entity.Community;
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
@DisplayName("CommunityService 통합 테스트")
class CommunityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("community_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CommunityService communityService;

    private Community testCommunity;
    private Map<String, Object> searchMap;
    private String uniqueUserId;

    @BeforeEach
    void setUp() {
        uniqueUserId = String.valueOf(System.currentTimeMillis() % Integer.MAX_VALUE);
        testCommunity = new Community();
        testCommunity.setUserNm("통합테스트 사용자_" + uniqueUserId);
        testCommunity.setContent("통합 테스트 게시글 내용입니다. " + System.currentTimeMillis());
        testCommunity.setUserId(String.valueOf(uniqueUserId));
        testCommunity.setHeartrate(72);
        testCommunity.setTemperature(36.5);
        testCommunity.setBloodpress(120.0);
        testCommunity.setSmoking(0);
        testCommunity.setDrinking(0);
        testCommunity.setExercise(30);
        testCommunity.setAge(25);
        testCommunity.setBodyAge(23);

        searchMap = new HashMap<>();
        searchMap.put("pageIdx", 1);
        searchMap.put("age", 25);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        testCommunity = null;
        searchMap = null;
    }

    @Test
    @DisplayName("게시글 작성 및 조회 통합 테스트")
    void testWriteAndFindBoard() {
        int writeResult = communityService.writeBoard(testCommunity);
        assertThat(writeResult).isGreaterThan(0);

        Community foundBoard = communityService.findBoard(testCommunity.getCommuSeq());
        assertThat(foundBoard).isNotNull();
        assertThat(foundBoard.getContent()).contains("통합 테스트 게시글 내용입니다.");
    }

    @Test
    @DisplayName("게시글 목록 조회 통합 테스트")
    void testFindBoardList() {
        List<Community> boardList = communityService.findBoardList(searchMap);
        assertThat(boardList).isNotNull();
    }

    @Test
    @DisplayName("게시글 전체 플로우 통합 테스트")
    void testCommunityFlow() {
        Community uniqueCommunity = new Community();
        uniqueCommunity.setUserNm("플로우테스트_" + System.currentTimeMillis());
        uniqueCommunity.setContent("플로우 테스트 게시글 " + System.currentTimeMillis());
        uniqueCommunity.setUserId(String.valueOf(System.currentTimeMillis() % Integer.MAX_VALUE));
        uniqueCommunity.setHeartrate(72);
        uniqueCommunity.setTemperature(36.5);
        uniqueCommunity.setBloodpress(120.0);
        uniqueCommunity.setSmoking(0);
        uniqueCommunity.setDrinking(0);
        uniqueCommunity.setExercise(30);
        uniqueCommunity.setAge(25);
        uniqueCommunity.setBodyAge(23);

        int writeResult = communityService.writeBoard(uniqueCommunity);
        assertThat(writeResult).isGreaterThan(0);

        Community foundBoard = communityService.findBoard(uniqueCommunity.getCommuSeq());
        assertThat(foundBoard).isNotNull();

        Map<String, Object> uniqueSearchMap = new HashMap<>();
        uniqueSearchMap.put("pageIdx", 1);
        uniqueSearchMap.put("age", 25);
        List<Community> boardList = communityService.findBoardList(uniqueSearchMap);
        assertThat(boardList).isNotNull();
    }
}

