package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityRepository 테스트")
class CommunityRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Community> typedQuery;

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
        doNothing().when(entityManager).persist(any(Community.class));

        int result = communityRepository.writeBoard(testCommunity);

        assertThat(result).isEqualTo(1);
        verify(entityManager, times(1)).persist(testCommunity);
    }

    @Test
    @DisplayName("게시글 저장 실패 테스트")
    void writeBoard_Failure() {
        doThrow(new RuntimeException("데이터베이스 오류")).when(entityManager).persist(any(Community.class));

        int result = communityRepository.writeBoard(testCommunity);

        assertThat(result).isEqualTo(0);
        verify(entityManager, times(1)).persist(testCommunity);
    }

    @Test
    @DisplayName("게시글 저장 - null 입력 테스트")
    void writeBoard_NullInput() {
        doThrow(new IllegalArgumentException("Entity cannot be null")).when(entityManager).persist(null);

        int result = communityRepository.writeBoard(null);

        assertThat(result).isEqualTo(0);
        verify(entityManager, times(1)).persist(null);
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 테스트")
    void findBoard_Success() {
        when(entityManager.find(Community.class, 1)).thenReturn(testCommunity);

        Community result = communityRepository.findBoard(1);

        assertThat(result).isNotNull();
        assertThat(result.getUserNm()).isEqualTo("testUser");
        assertThat(result.getContent()).isEqualTo("테스트 게시글 내용입니다.");
        verify(entityManager, times(1)).find(Community.class, 1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않는 게시글 테스트")
    void findBoard_NotFound() {
        when(entityManager.find(Community.class, 999)).thenReturn(null);

        Community result = communityRepository.findBoard(999);

        assertThat(result).isNull();
        verify(entityManager, times(1)).find(Community.class, 999);
    }

    @Test
    @DisplayName("게시글 상세 조회 중 예외 발생 테스트")
    void findBoard_Exception() {
        when(entityManager.find(Community.class, 1)).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        try {
            communityRepository.findBoard(1);
            fail("예외가 발생해야 합니다");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("데이터베이스 연결 오류");
        }

        verify(entityManager, times(1)).find(Community.class, 1);
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

        when(entityManager.createQuery(anyString(), eq(Community.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(communityList);

        List<Community> result = communityRepository.findBoardList(new java.util.HashMap<>());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserNm()).isEqualTo("user1");
        assertThat(result.get(0).getContent()).isEqualTo("첫 번째 게시글");
        assertThat(result.get(1).getUserNm()).isEqualTo("user2");
        assertThat(result.get(1).getContent()).isEqualTo("두 번째 게시글");

        verify(entityManager, times(1)).createQuery("select h from health_community h", Community.class);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 목록 테스트")
    void findBoardList_Empty() {
        when(entityManager.createQuery(anyString(), eq(Community.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        List<Community> result = communityRepository.findBoardList(new java.util.HashMap<>());

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(entityManager, times(1)).createQuery("select h from health_community h", Community.class);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("게시글 목록 조회 중 예외 발생 테스트")
    void findBoardList_Exception() {
        when(entityManager.createQuery(anyString(), eq(Community.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new RuntimeException("쿼리 실행 오류"));

        try {
            communityRepository.findBoardList(new java.util.HashMap<>());
            fail("예외가 발생해야 합니다");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("쿼리 실행 오류");
        }

        verify(entityManager, times(1)).createQuery("select h from health_community h", Community.class);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("게시글 저장 - EntityManager persist 호출 검증")
    void writeBoard_EntityManagerPersistCall() {
        Community newCommunity = new Community();
        newCommunity.setUserNm("newUser");
        newCommunity.setContent("새로운 게시글");

        doNothing().when(entityManager).persist(any(Community.class));

        communityRepository.writeBoard(newCommunity);

        verify(entityManager, times(1)).persist(newCommunity);
        verify(entityManager, never()).merge(any(Community.class));
        verify(entityManager, never()).remove(any(Community.class));
    }

    @Test
    @DisplayName("게시글 조회 - EntityManager find 호출 검증")
    void findBoard_EntityManagerFindCall() {
        when(entityManager.find(Community.class, 123)).thenReturn(testCommunity);

        communityRepository.findBoard(123);

        verify(entityManager, times(1)).find(Community.class, 123);
        verify(entityManager, never()).persist(any(Community.class));
        verify(entityManager, never()).merge(any(Community.class));
    }

    @Test
    @DisplayName("게시글 목록 조회 - JPQL 쿼리 검증")
    void findBoardList_JPQLQueryVerification() {
        when(entityManager.createQuery(anyString(), eq(Community.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(testCommunity));

        communityRepository.findBoardList(new java.util.HashMap<>());

        verify(entityManager, times(1)).createQuery(anyString(), eq(Community.class));
        verify(typedQuery, times(1)).getResultList();
        verify(typedQuery, never()).setParameter(anyString(), any());
        verify(typedQuery, never()).setMaxResults(anyInt());
    }
}
