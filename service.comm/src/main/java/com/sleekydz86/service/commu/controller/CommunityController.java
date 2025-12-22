package com.sleekydz86.service.commu.controller;

import com.sleekydz86.service.commu.entity.Community;
import com.sleekydz86.service.commu.dto.ApiResponse;
import com.sleekydz86.service.commu.dto.ApiResultCode;
import com.sleekydz86.service.commu.dto.CommunityRequestDto;
import com.sleekydz86.service.commu.service.CommunityService;
import com.sleekydz86.service.commu.util.DtoConverter;
import com.sleekydz86.service.commu.util.InputSanitizer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/community/v1/")
@RequiredArgsConstructor
@Validated
public class CommunityController {

    private final CommunityService communityService;
    private final InputSanitizer inputSanitizer;
    private final DtoConverter dtoConverter;

    @PostMapping("writeBoard")
    public ResponseEntity<ApiResponse<?>> writeBoard(@Valid @RequestBody CommunityRequestDto requestDto) {
        try {
            if (inputSanitizer.containsSqlInjection(requestDto.getContent()) || 
                inputSanitizer.containsXss(requestDto.getContent())) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            
            String sanitizedUserId = inputSanitizer.sanitizeUserId(requestDto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(requestDto.getUserId())) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            requestDto.setUserId(sanitizedUserId);
            
            Community community = dtoConverter.convertToEntity(requestDto, Community.class);
            int result = communityService.writeBoard(community);

            if (result > 0) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
                return ResponseEntity.ok(responseEntity.getBody());
            } else {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.INSERT_FAIL);
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
    }

    @PostMapping("findBoard")
    public ResponseEntity<ApiResponse<?>> findBoard(@Valid @RequestBody Map<String, Object> map) {
        try {
            Object commuSeqObj = map.get("commuSeq");
            if (commuSeqObj == null) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            int commuSeq;
            try {
                commuSeq = Integer.parseInt(commuSeqObj.toString());
            } catch (NumberFormatException e) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            Community result = communityService.findBoard(commuSeq);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생", e);
            ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.UNKNOWN_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
    }

    @PostMapping("findBoardList")
    public ResponseEntity<ApiResponse<?>> findBoardList(@RequestBody(required = false) Map<String, Object> map) {
        try {
            if (map == null) {
                map = new java.util.HashMap<>();
            }

            List<Community> items = communityService.findBoardList(map);
            return ApiResponse.ok(items);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            ResponseEntity<ApiResponse<List<Community>>> responseEntity = ApiResponse.<List<Community>>error(ApiResultCode.UNKNOWN_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
    }

    @PostMapping("updateBoard")
    public ResponseEntity<ApiResponse<?>> updateBoard(@Valid @RequestBody CommunityRequestDto requestDto) {
        try {
            if (requestDto.getCommuSeq() == null || requestDto.getCommuSeq() < 1) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            
            if (inputSanitizer.containsSqlInjection(requestDto.getContent()) || 
                inputSanitizer.containsXss(requestDto.getContent())) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            
            String sanitizedUserId = inputSanitizer.sanitizeUserId(requestDto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(requestDto.getUserId())) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            requestDto.setUserId(sanitizedUserId);
            
            Community community = dtoConverter.convertToEntity(requestDto, Community.class);
            Community updatedCommu = communityService.findBoard(community.getCommuSeq());
            if (updatedCommu == null) {
                ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.RESULT_IS_EMPTY);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            updatedCommu.setContent(community.getContent());
            communityService.writeBoard(updatedCommu);

            return ApiResponse.ok(community);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            ResponseEntity<ApiResponse<Community>> responseEntity = ApiResponse.<Community>error(ApiResultCode.UNKNOWN_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
    }

    @PostMapping("deleteBoard")
    public ResponseEntity<ApiResponse<?>> deleteBoard(@Valid @RequestBody Map<String, Object> map) {
        try {
            Object commuSeqObj = map.get("commuSeq");
            if (commuSeqObj == null) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            int commuSeq;
            try {
                commuSeq = Integer.parseInt(commuSeqObj.toString());
            } catch (NumberFormatException e) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                return ResponseEntity.ok(responseEntity.getBody());
            }
            
            int result = communityService.deleteBoard(commuSeq);
            
            if (result == 1) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
                return ResponseEntity.ok(responseEntity.getBody());
            } else {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.RESULT_IS_EMPTY);
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
    }
}
