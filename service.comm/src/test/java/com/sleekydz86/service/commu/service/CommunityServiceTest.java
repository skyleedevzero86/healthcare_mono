package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.entity.Community;
import com.sleekydz86.service.commu.metrics.CommunityMetrics;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityService 테스트")
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityMetrics communityMetrics;

    @Mock
    private Timer timer;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Sample timerSample;

    @InjectMocks
    private CommunityServiceImpl communityService;

    private Community testCommunity;

    @BeforeEach
    void setUp() {
        testCommunity = new Community();
        testCommunity.setUserNm("testUser_" + System.currentTimeMillis());
        testCommunity.setContent("테스트 게시글 내용입니다. " + System.currentTimeMillis());
        testCommunity.setRegDate(new Date());
        testCommunity.setUserId(String.valueOf(System.currentTimeMillis() % Integer.MAX_VALUE));

        when(communityMetrics.startBoardPostProcessingTimer()).thenReturn(timerSample);
        when(communityMetrics.startBoardQueryTimer()).thenReturn(timerSample);
        when(communityMetrics.getBoardPostProcessingTime()).thenReturn(timer);
        when(communityMetrics.getBoardQueryTime()).thenReturn(timer);
        doNothing().when(timerSample).stop(any(Timer.class));
        doNothing().when(communityMetrics).incrementBoardPostsCreated();
        doNothing().when(communityMetrics).incrementBoardPostsRead();
        doNothing().when(communityMetrics).incrementBoardListQueries();
        doNothing().when(communityMetrics).incrementBoardPostsUpdated();
        doNothing().when(communityMetrics).incrementBoardPostsDeleted();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        testCommunity = null;
        reset(communityRepository, communityMetrics);
    }

    @Test
    @DisplayName("게시글 작성 성공 테스트")
    void writeBoard_Success() {
        when(communityRepository.writeBoard(any(Community.class))).thenReturn(1);

        int result = communityService.writeBoard(testCommunity);

        assertThat(result).isEqualTo(1);
        verify(communityRepository, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 작성 실패 테스트")
    void writeBoard_Failure() {
        when(communityRepository.writeBoard(any(Community.class))).thenReturn(0);

        int result = communityService.writeBoard(testCommunity);

        assertThat(result).isEqualTo(0);
        verify(communityRepository, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 작성 중 예외 발생 테스트")
    void writeBoard_Exception() {
        when(communityRepository.writeBoard(any(Community.class))).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        assertThatThrownBy(() -> communityService.writeBoard(testCommunity))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(communityRepository, times(1)).writeBoard(any(Community.class));
        verify(communityMetrics, times(1)).incrementBoardPostsCreated();
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 테스트")
    void findBoard_Success() {
        when(communityRepository.findBoard(anyInt())).thenReturn(testCommunity);

        Community result = communityService.findBoard(1);

        assertThat(result).isNotNull();
        assertThat(result.getUserNm()).startsWith("testUser_");
        assertThat(result.getContent()).contains("테스트 게시글 내용입니다.");
        verify(communityRepository, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않는 게시글 테스트")
    void findBoard_NotFound() {
        when(communityRepository.findBoard(anyInt())).thenReturn(null);

        Community result = communityService.findBoard(999);

        assertThat(result).isNull();
        verify(communityRepository, times(1)).findBoard(999);
    }

    @Test
    @DisplayName("게시글 상세 조회 중 예외 발생 테스트")
    void findBoard_Exception() {
        when(communityRepository.findBoard(anyInt())).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        assertThatThrownBy(() -> communityService.findBoard(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(communityRepository, times(1)).findBoard(1);
        verify(communityMetrics, times(1)).incrementBoardPostsRead();
    }

    @Test
    @DisplayName("게시글 목록 조회 성공 테스트")
    void findBoardList_Success() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pageIdx", 1);
        map.put("age", 25);

        Community community1 = new Community();
        community1.setUserNm("user1");
        community1.setContent("첫 번째 게시글");

        Community community2 = new Community();
        community2.setUserNm("user2");
        community2.setContent("두 번째 게시글");

        List<Community> communityList = Arrays.asList(community1, community2);
        when(communityRepository.findBoardList(any())).thenReturn(communityList);

        List<Community> result = communityService.findBoardList(map);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserNm()).isEqualTo("user1");
        assertThat(result.get(0).getContent()).isEqualTo("첫 번째 게시글");
        assertThat(result.get(1).getUserNm()).isEqualTo("user2");
        assertThat(result.get(1).getContent()).isEqualTo("두 번째 게시글");
        verify(communityRepository, times(1)).findBoardList(any());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 목록 테스트")
    void findBoardList_Empty() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pageIdx", 1);
        map.put("age", 25);

        when(communityRepository.findBoardList(any())).thenReturn(Arrays.asList());

        List<Community> result = communityService.findBoardList(map);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(communityRepository, times(1)).findBoardList(any());
    }

    @Test
    @DisplayName("게시글 목록 조회 중 예외 발생 테스트")
    void findBoardList_Exception() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pageIdx", 1);
        map.put("age", 25);

        when(communityRepository.findBoardList(any())).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        assertThatThrownBy(() -> communityService.findBoardList(map))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(communityRepository, times(1)).findBoardList(any());
        verify(communityMetrics, times(1)).incrementBoardListQueries();
    }
}
