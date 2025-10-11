package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.Usermng;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import com.sleekydz86.service.commu.repository.UserRepository;
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
    private UserRepository userRepository;

    @InjectMocks
    private CommunityServiceImpl communityService;

    private Community testCommunity;
    private Usermng testUser;

    @BeforeEach
    void setUp() {
        testUser = new Usermng("user001", "testUser", "test@example.com",
                "encryptedPassword", "salt", "testUser", "19900101",
                "01012345678", "IT", "170", "70", "A", "M",
                new Date(), "admin", new Date(), "admin",
                "webToken", "mobileToken", "Y", "Y", "profile.jpg");

        testCommunity = new Community();
        testCommunity.setUserNm("testUser");
        testCommunity.setContent("테스트 게시글 내용입니다.");
        testCommunity.setRegDate(new Date());
        testCommunity.setUserId(1);
    }

    @Test
    @DisplayName("게시글 작성 성공 테스트")
    void writeBoard_Success() {
        // given
        when(userRepository.findOne(anyInt())).thenReturn(testUser);
        when(communityRepository.writeBoard(any(Community.class))).thenReturn(1);

        // when
        int result = communityService.writeBoard(testCommunity);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(testCommunity.getUserNm()).isEqualTo("testUser");
        verify(userRepository, times(1)).findOne(1);
        verify(communityRepository, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 작성 실패 테스트")
    void writeBoard_Failure() {
        // given
        when(userRepository.findOne(anyInt())).thenReturn(testUser);
        when(communityRepository.writeBoard(any(Community.class))).thenReturn(0);

        // when
        int result = communityService.writeBoard(testCommunity);

        // then
        assertThat(result).isEqualTo(0);
        verify(userRepository, times(1)).findOne(1);
        verify(communityRepository, times(1)).writeBoard(testCommunity);
    }

    @Test
    @DisplayName("게시글 작성 - 사용자 조회 실패 테스트")
    void writeBoard_UserNotFound() {
        // given
        when(userRepository.findOne(anyInt())).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> communityService.writeBoard(testCommunity))
                .isInstanceOf(NullPointerException.class);

        verify(userRepository, times(1)).findOne(1);
        verify(communityRepository, never()).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 작성 중 예외 발생 테스트")
    void writeBoard_Exception() {
        // given
        when(userRepository.findOne(anyInt())).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        assertThatThrownBy(() -> communityService.writeBoard(testCommunity))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(userRepository, times(1)).findOne(1);
        verify(communityRepository, never()).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 테스트")
    void findBoard_Success() {
        // given
        when(communityRepository.findBoard(anyInt())).thenReturn(testCommunity);

        // when
        Community result = communityService.findBoard(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserNm()).isEqualTo("testUser");
        assertThat(result.getContent()).isEqualTo("테스트 게시글 내용입니다.");
        verify(communityRepository, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않는 게시글 테스트")
    void findBoard_NotFound() {
        // given
        when(communityRepository.findBoard(anyInt())).thenReturn(null);

        // when
        Community result = communityService.findBoard(999);

        // then
        assertThat(result).isNull();
        verify(communityRepository, times(1)).findBoard(999);
    }

    @Test
    @DisplayName("게시글 상세 조회 중 예외 발생 테스트")
    void findBoard_Exception() {
        // given
        when(communityRepository.findBoard(anyInt())).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        assertThatThrownBy(() -> communityService.findBoard(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(communityRepository, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 목록 조회 성공 테스트")
    void findBoardList_Success() {
        // given
        Community community1 = new Community();
        community1.setUserNm("user1");
        community1.setContent("첫 번째 게시글");

        Community community2 = new Community();
        community2.setUserNm("user2");
        community2.setContent("두 번째 게시글");

        List<Community> communityList = Arrays.asList(community1, community2);
        when(communityRepository.findBoardList()).thenReturn(communityList);

        // when
        List<Community> result = communityService.findBoardList();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserNm()).isEqualTo("user1");
        assertThat(result.get(0).getContent()).isEqualTo("첫 번째 게시글");
        assertThat(result.get(1).getUserNm()).isEqualTo("user2");
        assertThat(result.get(1).getContent()).isEqualTo("두 번째 게시글");
        verify(communityRepository, times(1)).findBoardList();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 목록 테스트")
    void findBoardList_Empty() {
        // given
        when(communityRepository.findBoardList()).thenReturn(Arrays.asList());

        // when
        List<Community> result = communityService.findBoardList();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(communityRepository, times(1)).findBoardList();
    }

    @Test
    @DisplayName("게시글 목록 조회 중 예외 발생 테스트")
    void findBoardList_Exception() {
        // given
        when(communityRepository.findBoardList()).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        assertThatThrownBy(() -> communityService.findBoardList())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(communityRepository, times(1)).findBoardList();
    }
}
