package com.sleekydz86.service.commu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.DiseaseCategory;
import com.sleekydz86.service.commu.domain.Usermng;
import com.sleekydz86.service.commu.domain.dto.ApiResultCode;
import com.sleekydz86.service.commu.service.CommunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityController.class)
@DisplayName("CommunityController MockMvc 테스트")
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityService communityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Community testCommunity;

    @BeforeEach
    void setUp() {
        testCommunity = new Community();
        testCommunity.setUserNm("testUser");
        testCommunity.setContent("테스트 게시글 내용입니다.");
        testCommunity.setRegDate(new Date());
    }

    @Test
    @DisplayName("게시글 작성 성공 테스트")
    void writeBoard_Success() throws Exception {
        // given
        when(communityService.writeBoard(any(Community.class))).thenReturn(1);

        // when & then
        mockMvc.perform(post("/community/v1/writeBoard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommunity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 작성 실패 테스트")
    void writeBoard_Failure() throws Exception {
        // given
        when(communityService.writeBoard(any(Community.class))).thenReturn(0);

        // when & then
        mockMvc.perform(post("/community/v1/writeBoard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommunity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.INSERT_FAIL.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.INSERT_FAIL.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 작성 중 예외 발생 테스트")
    void writeBoard_Exception() throws Exception {
        // given
        when(communityService.writeBoard(any(Community.class)))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        mockMvc.perform(post("/community/v1/writeBoard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommunity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.UNKOWN_ERR.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.UNKOWN_ERR.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 테스트")
    void findBoard_Success() throws Exception {
        // given
        when(communityService.findBoard(anyInt())).thenReturn(testCommunity);

        // when & then
        mockMvc.perform(post("/community/v1/findBoard")
                .param("commuSeq", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData.userNm").value("testUser"))
                .andExpect(jsonPath("$.resultData.content").value("테스트 게시글 내용입니다."));

        verify(communityService, times(1)).findBoard(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않는 게시글 테스트")
    void findBoard_NotFound() throws Exception {
        // given
        when(communityService.findBoard(anyInt())).thenReturn(null);

        // when & then
        mockMvc.perform(post("/community/v1/findBoard")
                .param("commuSeq", "999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).findBoard(999);
    }

    @Test
    @DisplayName("게시글 목록 조회 성공 테스트")
    void findBoardList_Success() throws Exception {
        // given
        Community community1 = new Community();
        community1.setUserNm("user1");
        community1.setContent("첫 번째 게시글");

        Community community2 = new Community();
        community2.setUserNm("user2");
        community2.setContent("두 번째 게시글");

        List<Community> communityList = Arrays.asList(community1, community2);
        when(communityService.findBoardList()).thenReturn(communityList);

        // when & then
        mockMvc.perform(post("/community/v1/findBoardList"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData").isArray())
                .andExpect(jsonPath("$.resultData.length()").value(2))
                .andExpect(jsonPath("$.resultData[0].userNm").value("user1"))
                .andExpect(jsonPath("$.resultData[0].content").value("첫 번째 게시글"))
                .andExpect(jsonPath("$.resultData[1].userNm").value("user2"))
                .andExpect(jsonPath("$.resultData[1].content").value("두 번째 게시글"));

        verify(communityService, times(1)).findBoardList();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 목록 테스트")
    void findBoardList_Empty() throws Exception {
        // given
        when(communityService.findBoardList()).thenReturn(Arrays.asList());

        // when & then
        mockMvc.perform(post("/community/v1/findBoardList"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData").isArray())
                .andExpect(jsonPath("$.resultData.length()").value(0));

        verify(communityService, times(1)).findBoardList();
    }

    @Test
    @DisplayName("게시글 수정 성공 테스트")
    void updateBoard_Success() throws Exception {
        // given
        Community existingCommunity = new Community();
        existingCommunity.setUserNm("testUser");
        existingCommunity.setContent("원본 내용");

        when(communityService.findBoard(1)).thenReturn(existingCommunity);
        when(communityService.writeBoard(any(Community.class))).thenReturn(1);

        // when & then
        mockMvc.perform(post("/community/v1/updateBoard")
                .param("commuSeq", "1")
                .param("content", "수정된 내용"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.SUCCESS.message))
                .andExpect(jsonPath("$.resultData.content").value("수정된 내용"));

        verify(communityService, times(1)).findBoard(1);
        verify(communityService, times(1)).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 수정 - 존재하지 않는 게시글 테스트")
    void updateBoard_NotFound() throws Exception {
        // given
        when(communityService.findBoard(anyInt())).thenReturn(null);

        // when & then
        mockMvc.perform(post("/community/v1/updateBoard")
                .param("commuSeq", "999")
                .param("content", "수정된 내용"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.UNKOWN_ERR.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.UNKOWN_ERR.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).findBoard(999);
        verify(communityService, never()).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 수정 중 예외 발생 테스트")
    void updateBoard_Exception() throws Exception {
        // given
        when(communityService.findBoard(anyInt()))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        mockMvc.perform(post("/community/v1/updateBoard")
                .param("commuSeq", "1")
                .param("content", "수정된 내용"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.UNKOWN_ERR.code))
                .andExpect(jsonPath("$.resultMessage").value(ApiResultCode.UNKOWN_ERR.message))
                .andExpect(jsonPath("$.resultData").isEmpty());

        verify(communityService, times(1)).findBoard(1);
        verify(communityService, never()).writeBoard(any(Community.class));
    }

    @Test
    @DisplayName("게시글 작성 - 필수 필드 누락 테스트")
    void writeBoard_MissingFields() throws Exception {
        // given
        Community incompleteCommunity = new Community();
        incompleteCommunity.setUserNm("testUser");

        
        // when & then
        mockMvc.perform(post("/community/v1/writeBoard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteCommunity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(ApiResultCode.SUCCESS.code));

        verify(communityService, times(1)).writeBoard(any(Community.class));
    }

}
