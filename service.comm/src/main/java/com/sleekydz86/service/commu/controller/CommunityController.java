package com.sleekydz86.service.commu.controller;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.dto.ApiResponse;
import com.sleekydz86.service.commu.domain.dto.ApiResultCode;
import com.sleekydz86.service.commu.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse> writeBoard(@RequestBody Community community) {
        try {
            int result = communityService.writeBoard(community);
            if(result == 1) {
                return ApiResponse.ok();
            } else {
                return ApiResponse.error(ApiResultCode.INSERT_FAIL);
            }
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("findBoard")
    public ResponseEntity<ApiResponse> findBoard(Long id) {
        return ApiResponse.ok(communityService.findBoard(id));

    }

}
