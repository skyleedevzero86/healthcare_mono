package com.sleekydz86.service.usermanagement.controller;

import com.sleekydz86.service.usermanagement.dto.ApiResponse;
import com.sleekydz86.service.usermanagement.dto.ApiResultCode;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.global.util.AES256Util;
import com.sleekydz86.service.usermanagement.global.util.PagingUtil;
import com.sleekydz86.service.usermanagement.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management")
@Slf4j
public class ManagementController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    PagingUtil pagingUtil;
    @Autowired
    com.sleekydz86.service.usermanagement.util.InputSanitizer inputSanitizer;

    @PostMapping("/v1/userInfo")
    public ResponseEntity<ApiResponse> userInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            return ApiResponse.ok(userService.userInfo(dto));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/userBoardInfo")
    public ResponseEntity<ApiResponse> userBoardInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {

        Map<String, Object> responseData = new HashMap<>();

        log.info("사용자 게시판 정보 요청 : " + dto.toString());
        Map<String, Object> userBioinfo = userService.userInfo(dto);
        List<Map<String, Object>> userRoleinfo = userService.searchdrguardianList(dto);

        UserhealthDto userhealthDto = new UserhealthDto();
        userhealthDto.setBirthdate(AES256Util.decrypt((String) userBioinfo.get("birthEnc")));
        userhealthDto.setGender((String) userBioinfo.get("gender"));

        Map<String, Object> userHealthavg = userService.ageavgHealthinfo(userhealthDto);

        responseData.put("userBioinfo", userBioinfo);
        responseData.put("userRolelist", userRoleinfo);
        responseData.put("userHealthavg", userHealthavg);

        log.info("사용자 게시판 정보 결과 : " + responseData.toString());

        if (userBioinfo.isEmpty()) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            return ApiResponse.ok(responseData);
        }
    }

    @PostMapping("/v1/updateUserInfo")
    public ResponseEntity<ApiResponse> updateUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            int result = userService.updateUserInfo(dto);

        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 수정 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/deleteUserInfo")
    public ResponseEntity<ApiResponse> deleteUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            int result = userService.deleteUserInfo(dto);
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 삭제 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/updatePasswd")
    public ResponseEntity<ApiResponse> updatePasswd(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            int result = userService.updatePasswd(dto);
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/searchDoctor")
    public ResponseEntity<ApiResponse> searchDoctor(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> list = userService.searchDoctor(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("의사 검색 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/searchParent")
    public ResponseEntity<ApiResponse> searchParent(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> list = userService.searchParent(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("보호자 검색 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @RequestMapping("/v1/list")
    public ResponseEntity<ApiResponse> userList(UserDto dto, @RequestBody Map<String, Object> map) throws Exception {
        try {
            if (map == null) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            String userRoleFk = (String) map.get("userRoleFk");
            if (userRoleFk == null) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            Object pageIdxObj = map.get("pageIdx");
            Object pageOffsetObj = map.get("pageOffset");
            if (pageIdxObj == null || pageOffsetObj == null) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            dto.setUserRoleFk(userRoleFk);
            dto.setPageIndex((int) pageIdxObj);
            dto.setPageOffset((int) pageOffsetObj);
            
            String searchKeyword = (String) map.get("searchKeyword");
            if (searchKeyword != null) {
                if (inputSanitizer.containsSqlInjection(searchKeyword) || 
                    inputSanitizer.containsXss(searchKeyword)) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setSearchKeyword(inputSanitizer.sanitize(searchKeyword));
            }

            if ("3".equals(userRoleFk)) {
            return ApiResponse.ok(userService.doctorList(dto));
        } else if (map.get("userRoleFk").equals("2")) {
            return ApiResponse.ok(userService.parentList(dto));
        } else if (map.get("userRoleFk").equals("1")) {
            dto.setUserId((String) map.getOrDefault("userId", null));
            return ApiResponse.ok(userService.userList(dto));
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/healthUserList")
    public ResponseEntity<ApiResponse> searchHealthUserList(@RequestBody Map<String, Object> map) {
        try {
            if (map != null && map.get("userId") != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(map.get("userId").toString());
                map.put("userId", sanitizedUserId);
            }
            return ApiResponse.ok(userService.searchHealthUserList(map));
        } catch (Exception e) {
            log.error("건강 사용자 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/drguardianList")
    public ResponseEntity<ApiResponse> drguardianList(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            return ApiResponse.ok(userService.searchdrguardianList(dto));
        } catch (Exception e) {
            log.error("의사/보호자 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/manage_userList")
    public ResponseEntity<ApiResponse> roleuserList(@RequestBody Map<String, Object> map) {
        try {
            if (map == null) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            UserDto dto = new UserDto();
            if (map.get("userId") != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(map.get("userId").toString());
                dto.setUserId(sanitizedUserId);
            }
            dto.setUserRoleFk((String) map.get("userRoleFk"));
            
            Object pageIdxObj = map.get("pageIdx");
            Object pageOffsetObj = map.get("pageOffset");
            if (pageIdxObj != null && pageOffsetObj != null) {
                dto.setPageIndex((int) pageIdxObj);
                dto.setPageOffset((int) pageOffsetObj);
            }
            
            String searchKeyword = (String) map.get("searchKeyword");
            if (searchKeyword != null) {
                if (inputSanitizer.containsSqlInjection(searchKeyword) || 
                    inputSanitizer.containsXss(searchKeyword)) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setSearchKeyword(inputSanitizer.sanitize(searchKeyword));
            }

            return ApiResponse.ok(userService.manage_userList(dto));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("역할별 사용자 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/search_userList")
    public ResponseEntity<ApiResponse> searchuserList(@RequestBody Map<String, Object> map) {
        try {
            if (map != null && map.get("userId") != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(map.get("userId").toString());
                map.put("userId", sanitizedUserId);
            }
            if (map != null && map.get("searchKeyword") != null) {
                String searchKeyword = map.get("searchKeyword").toString();
                if (inputSanitizer.containsSqlInjection(searchKeyword) || 
                    inputSanitizer.containsXss(searchKeyword)) {
                    return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
                }
                map.put("searchKeyword", inputSanitizer.sanitize(searchKeyword));
            }
            return ApiResponse.ok(userService.searchuserList(map));
        } catch (Exception e) {
            log.error("사용자 검색 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }
}