package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Community;
import com.sleekydz86.service.commu.mapper.CommunityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityRepository 테스트")
class CommunityRepositoryTest {

    @Mock
    private CommunityMapper communityMapper;

    @InjectMocks
    private CommunityRepository communityRepository;

    private Community testCommunity;

    @BeforeEach
    void setUp() {
        testCommunity = new Community();
        testCommunity.setUserNm("testUser");
        testCommunity.setContent("테스트 게시글 내용입니다.");
        testCommunity.setRegDate(new Date());
        testCommunity.setUserId("1");
    }

    @Test
    @DisplayName("게시글 저장 성공 테스트")
    void writeBoard_Success() {
        testCommunity.setCommuSeq(1);
        when(communityMapper.writeBoard(any(Community.class))).thenAnswer(invocation -> {
            Community c = invocation.getArgument(0);
            c.setCommuSeq(1);
            return 1;
        });

        int result = communityRepository.writeBoard(testCommunity);

        assertThat(result).isEqualTo(1);
        verify(communityMapper, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 저장 실패 테스트")
    void writeBoard_Failure() {
        doThrow(new RuntimeException("데이터베이스 오류")).when(communityMapper).writeBoard(any(Community.class));

        try {
            communityRepository.writeBoard(testCommunity);
            fail("예외가 발생해야 합니다");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

        verify(communityMapper, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 저장 - null 입력 테스트")
    void writeBoard_NullInput() {
        doThrow(new IllegalArgumentException("Community cannot be null")).when(communityMapper).writeBoard(null);

        try {
            communityRepository.writeBoard(null);
            fail("예외가 발생해야 합니다");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }

        verify(communityMapper, times(1)).writeBoard(null);
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 테스트")
    void findBoard_Success() {
        when(communityMapper.findBoard(1)).thenReturn(testCommunity);

        Community result = communityRepository.findBoard(1);

        assertThat(result).isNotNull();
        assertThat(result.getUserNm()).isEqualTo("testUser");
        assertThat(result.getContent()).isEqualTo("테스트 게시글 내용입니다.");
        verify(communityMapper, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않는 게시글 테스트")
    void findBoard_NotFound() {
        when(communityMapper.findBoard(999)).thenReturn(null);

        Community result = communityRepository.findBoard(999);

        assertThat(result).isNull();
        verify(communityMapper, times(1)).findBoard(999);
    }

    @Test
    @DisplayName("게시글 상세 조회 중 예외 발생 테스트")
    void findBoard_Exception() {
        when(communityMapper.findBoard(1)).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        try {
            communityRepository.findBoard(1);
            fail("예외가 발생해야 합니다");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("데이터베이스 연결 오류");
        }

        verify(communityMapper, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 목록 조회 성공 테스트")
    void findBoardList_Success() {
        Community community1 = new Community();
        community1.setUserNm("user1");
        community1.setContent("첫 번째 게시글");

        Community community2 = new Community();
        community2.setUserNm("user2");
        community2.setContent("두 번째 게시글");

        List<Community> communityList = Arrays.asList(community1, community2);

        when(communityMapper.findBoardList(anyMap())).thenReturn(communityList);

        List<Community> result = communityRepository.findBoardList(new HashMap<>());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserNm()).isEqualTo("user1");
        assertThat(result.get(0).getContent()).isEqualTo("첫 번째 게시글");
        assertThat(result.get(1).getUserNm()).isEqualTo("user2");
        assertThat(result.get(1).getContent()).isEqualTo("두 번째 게시글");

        verify(communityMapper, times(1)).findBoardList(anyMap());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 목록 테스트")
    void findBoardList_Empty() {
        when(communityMapper.findBoardList(anyMap())).thenReturn(Arrays.asList());

        List<Community> result = communityRepository.findBoardList(new HashMap<>());

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(communityMapper, times(1)).findBoardList(anyMap());
    }

    @Test
    @DisplayName("게시글 목록 조회 중 예외 발생 테스트")
    void findBoardList_Exception() {
        when(communityMapper.findBoardList(anyMap())).thenThrow(new RuntimeException("쿼리 실행 오류"));

        try {
            communityRepository.findBoardList(new HashMap<>());
            fail("예외가 발생해야 합니다");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("쿼리 실행 오류");
        }

        verify(communityMapper, times(1)).findBoardList(anyMap());
    }

    @Test
    @DisplayName("게시글 저장 - CommunityMapper writeBoard 호출 검증")
    void writeBoard_CommunityMapperCall() {
        Community newCommunity = new Community();
        newCommunity.setUserNm("newUser");
        newCommunity.setContent("새로운 게시글");
        newCommunity.setCommuSeq(1);

        when(communityMapper.writeBoard(any(Community.class))).thenAnswer(invocation -> {
            Community c = invocation.getArgument(0);
            c.setCommuSeq(1);
            return 1;
        });

        communityRepository.writeBoard(newCommunity);

        verify(communityMapper, times(1)).writeBoard(newCommunity);
    }

    @Test
    @DisplayName("게시글 조회 - CommunityMapper findBoard 호출 검증")
    void findBoard_CommunityMapperCall() {
        when(communityMapper.findBoard(123)).thenReturn(testCommunity);

        communityRepository.findBoard(123);

        verify(communityMapper, times(1)).findBoard(123);
    }

    @Test
    @DisplayName("게시글 목록 조회 - CommunityMapper findBoardList 호출 검증")
    void findBoardList_CommunityMapperCall() {
        when(communityMapper.findBoardList(anyMap())).thenReturn(Arrays.asList(testCommunity));

        communityRepository.findBoardList(new HashMap<>());

        verify(communityMapper, times(1)).findBoardList(anyMap());
    }
}
