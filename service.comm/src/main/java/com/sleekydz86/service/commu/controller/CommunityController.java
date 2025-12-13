package com.sleekydz86.service.commu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.dto.ApiResponse;
import com.sleekydz86.service.commu.domain.dto.ApiResultCode;
import com.sleekydz86.service.commu.dto.CommunityRequestDto;
import com.sleekydz86.service.commu.service.CommunityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/community/v1/")
@Validated
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    com.sleekydz86.service.commu.util.InputSanitizer inputSanitizer;

    @PostMapping("writeBoard")
    public ResponseEntity<ApiResponse> writeBoard(@Valid @RequestBody CommunityRequestDto requestDto) {
        try {
            if (inputSanitizer.containsSqlInjection(requestDto.getContent()) || 
                inputSanitizer.containsXss(requestDto.getContent())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            String sanitizedUserId = inputSanitizer.sanitizeUserId(requestDto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(requestDto.getUserId())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            requestDto.setUserId(sanitizedUserId);
            
            ObjectMapper obj = new ObjectMapper();
            Community community = obj.convertValue(requestDto, Community.class);
            int result = communityService.writeBoard(community);

            if (result == 1) {
                return ApiResponse.ok();
            } else {
                return ApiResponse.error(ApiResultCode.INSERT_FAIL);
            }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("findBoard")
    public ResponseEntity<ApiResponse> findBoard(@Valid @RequestBody Map<String, Object> map) {
        try {
            Object commuSeqObj = map.get("commuSeq");
            if (commuSeqObj == null) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            int commuSeq;
            try {
                commuSeq = Integer.parseInt(commuSeqObj.toString());
            } catch (NumberFormatException e) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            return ApiResponse.ok(communityService.findBoard(commuSeq));
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

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
    public ResponseEntity<ApiResponse> updateBoard(@Valid @RequestBody CommunityRequestDto requestDto) {
        try {
            if (requestDto.getCommuSeq() == null || requestDto.getCommuSeq() < 1) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            if (inputSanitizer.containsSqlInjection(requestDto.getContent()) || 
                inputSanitizer.containsXss(requestDto.getContent())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            String sanitizedUserId = inputSanitizer.sanitizeUserId(requestDto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(requestDto.getUserId())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            requestDto.setUserId(sanitizedUserId);
            
            ObjectMapper obj = new ObjectMapper();
            Community community = obj.convertValue(requestDto, Community.class);
            Community updatedCommu = communityService.findBoard(community.getCommuSeq());
            if (updatedCommu == null) {
                return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
            }
            updatedCommu.setContent(community.getContent());
            communityService.writeBoard(updatedCommu);

            return ApiResponse.ok(community);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }
}
