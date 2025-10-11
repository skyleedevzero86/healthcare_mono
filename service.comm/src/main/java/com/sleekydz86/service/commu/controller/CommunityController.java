package com.sleekydz86.service.commu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.dto.ApiResponse;
import com.sleekydz86.service.commu.domain.dto.ApiResultCode;
import com.sleekydz86.service.commu.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/community/v1/")
public class CommunityController {

    @Autowired
    CommunityService communityService;

    /**
     * 커뮤니티 보드 INSERT
     */
    @PostMapping("writeBoard")
    public ResponseEntity<ApiResponse> writeBoard(@RequestBody Map<String, Object> map) {
        try {
            log.info("ash get param " + map.toString());

            ObjectMapper obj = new ObjectMapper();
            Community community = obj.convertValue(map, Community.class);
            int result = communityService.writeBoard(community);

            if (result == 1) {
                return ApiResponse.ok();
            } else {
                return ApiResponse.error(ApiResultCode.INSERT_FAIL);
            }
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    /**
     * community 게시글 상세보기
     * 
     * @param commuSeq
     * @return
     */

    @PostMapping("findBoard")
    public ResponseEntity<ApiResponse> findBoard(int commuSeq) {
        return ApiResponse.ok(communityService.findBoard(commuSeq));
    }

    /**
     * community 게시글 전체 보기
     * 
     * @return
     */
    @PostMapping("findBoardList")
    public ResponseEntity<ApiResponse> findBoardList(@RequestBody(required = false) Map<String, Object> map) {
        try {
            if (map == null) {
                map = new java.util.HashMap<>();
            }

            List<Community> items = communityService.findBoardList(map);
            return ApiResponse.ok(items);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("updateBoard")
    public ResponseEntity<ApiResponse> updateBoard(@ModelAttribute Community community) {
        try {
            Community updatedCommu = communityService.findBoard(community.getCommuSeq());
            updatedCommu.setContent(community.getContent());
            // updatedCommu.setCategory(community.getCategory());
            communityService.writeBoard(updatedCommu);

            return ApiResponse.ok(community);
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }
}