package com.sleekydz86.service.usermanagement.controller;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.ApiResponse;
import com.sleekydz86.service.usermanagement.dto.ApiResultCode;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.global.util.AES256Util;
import com.sleekydz86.service.usermanagement.service.health.UserHealthService;
import com.sleekydz86.service.usermanagement.service.relationship.UserRelationshipService;
import com.sleekydz86.service.usermanagement.service.search.UserSearchService;
import com.sleekydz86.service.usermanagement.service.user.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserManagementService userManagementService;
    private final UserSearchService userSearchService;
    private final UserRelationshipService userRelationshipService;
    private final UserHealthService userHealthService;
    private final com.sleekydz86.service.usermanagement.util.InputSanitizer inputSanitizer;

    @PostMapping("/v1/userInfo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto) {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<Map<String, Object>> response = userManagementService.getUserInfo(dto);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/userBoardInfo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userBoardInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto) {

        Map<String, Object> responseData = new HashMap<>();

        log.info("사용자 게시판 정보 요청 : " + dto.toString());
        ServiceResponse<Map<String, Object>> userBioinfoResponse = userManagementService.getUserInfo(dto);
        if (!userBioinfoResponse.isSuccess()) {
            return convertToApiResponse(userBioinfoResponse);
        }
        Map<String, Object> userBioinfo = userBioinfoResponse.getData();
        
        ServiceResponse<List<Map<String, Object>>> userRoleinfoResponse = userSearchService.searchDoctorGuardianList(dto);
        if (!userRoleinfoResponse.isSuccess()) {
            return convertToApiResponse(userRoleinfoResponse);
        }
        List<Map<String, Object>> userRoleinfo = userRoleinfoResponse.getData();

        UserhealthDto userhealthDto = new UserhealthDto();
        userhealthDto.setBirthdate(AES256Util.decrypt((String) userBioinfo.get("birthEnc")));
        userhealthDto.setGender((String) userBioinfo.get("gender"));

        ServiceResponse<Map<String, Object>> userHealthavgResponse = userHealthService.getAgeAvgHealthInfo(userhealthDto);
        if (!userHealthavgResponse.isSuccess()) {
            return convertToApiResponse(userHealthavgResponse);
        }
        Map<String, Object> userHealthavg = userHealthavgResponse.getData();

        responseData.put("userBioinfo", userBioinfo);
        responseData.put("userRolelist", userRoleinfo);
        responseData.put("userHealthavg", userHealthavg);

        log.info("사용자 게시판 정보 결과 : " + responseData.toString());

        if (userBioinfo == null || userBioinfo.isEmpty()) {
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            return ApiResponse.ok(responseData);
        }
    }

    @PostMapping("/v1/updateUserInfo")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto) {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            
            ServiceResponse<Integer> guardianResponse = userRelationshipService.updateGuardianMapping(dto);
            if (!guardianResponse.isSuccess()) {
                return convertToApiResponse(guardianResponse);
            }
            
            ServiceResponse<Integer> doctorResponse = userRelationshipService.updateDoctorMapping(dto);
            if (!doctorResponse.isSuccess()) {
                return convertToApiResponse(doctorResponse);
            }
            
            ServiceResponse<Integer> response = userManagementService.updateUserInfo(dto);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            
            int result = response.getData();

        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.<Void>error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 수정 중 오류 발생", e);
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/deleteUserInfo")
    public ResponseEntity<ApiResponse<Void>> deleteUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto) {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<Integer> response = userManagementService.deleteUserInfo(dto);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            int result = response.getData();
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.<Void>error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 정보 삭제 중 오류 발생", e);
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/updatePasswd")
    public ResponseEntity<ApiResponse<Void>> updatePasswd(HttpServletRequest req, @Valid @RequestBody UserDto dto) {
        try {
            if (dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                    return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<Integer> response = userManagementService.updatePassword(dto);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            int result = response.getData();
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.<Void>error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/searchDoctor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchDoctor(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<List<Map<String, Object>>> response = userSearchService.searchDoctor(dto);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            List<Map<String, Object>> list = response.getData();
            Map<String, Object> result = new HashMap<>();
        if (list == null || list.size() == 0) {
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("의사 검색 중 오류 발생", e);
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/searchParent")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchParent(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<List<Map<String, Object>>> response = userSearchService.searchParent(dto);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            List<Map<String, Object>> list = response.getData();
            Map<String, Object> result = new HashMap<>();
        if (list == null || list.size() == 0) {
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("보호자 검색 중 오류 발생", e);
            return ApiResponse.<Map<String, Object>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @RequestMapping("/v1/list")
    public ResponseEntity<ApiResponse<?>> userList(UserDto dto, @RequestBody Map<String, Object> map) {
        try {
            if (map == null) {
                return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            String userRoleFk = (String) map.get("userRoleFk");
            if (userRoleFk == null) {
                return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            Object pageIdxObj = map.get("pageIdx");
            Object pageOffsetObj = map.get("pageOffset");
            if (pageIdxObj == null || pageOffsetObj == null) {
                return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            dto.setUserRoleFk(userRoleFk);
            dto.setPageIndex((int) pageIdxObj);
            dto.setPageOffset((int) pageOffsetObj);
            
            String searchKeyword = (String) map.get("searchKeyword");
            if (searchKeyword != null) {
                if (inputSanitizer.containsSqlInjection(searchKeyword) || 
                    inputSanitizer.containsXss(searchKeyword)) {
                    return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setSearchKeyword(inputSanitizer.sanitize(searchKeyword));
            }

            if ("3".equals(userRoleFk)) {
            ServiceResponse<Object> response = userManagementService.getDoctorList(dto);
            return convertToApiResponse(response);
        } else if (map.get("userRoleFk").equals("2")) {
            ServiceResponse<Object> response = userManagementService.getParentList(dto);
            return convertToApiResponse(response);
        } else if (map.get("userRoleFk").equals("1")) {
            dto.setUserId((String) map.getOrDefault("userId", null));
            ServiceResponse<Object> response = userManagementService.getUserList(dto);
            return convertToApiResponse(response);
        } else {
            return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/healthUserList")
    public ResponseEntity<ApiResponse<?>> searchHealthUserList(@RequestBody Map<String, Object> map) {
        try {
            if (map != null && map.get("userId") != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(map.get("userId").toString());
                map.put("userId", sanitizedUserId);
            }
            ServiceResponse<List<Map<String, Object>>> response = userSearchService.searchHealthUserList(map);
            return convertToApiResponse(response);
        } catch (Exception e) {
            log.error("건강 사용자 목록 조회 중 오류 발생", e);
            return ApiResponse.<List<Map<String, Object>>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/drguardianList")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> drguardianList(@RequestBody UserDto dto) {
        try {
            if (dto != null && dto.getUserId() != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
                dto.setUserId(sanitizedUserId);
            }
            ServiceResponse<List<Map<String, Object>>> response = userSearchService.searchDoctorGuardianList(dto);
            return convertToApiResponse(response);
        } catch (Exception e) {
            log.error("의사/보호자 목록 조회 중 오류 발생", e);
            return ApiResponse.<List<Map<String, Object>>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/manage_userList")
    public ResponseEntity<ApiResponse<Object>> roleuserList(@RequestBody Map<String, Object> map) {
        try {
            if (map == null) {
                return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
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
                    return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                dto.setSearchKeyword(inputSanitizer.sanitize(searchKeyword));
            }

            ServiceResponse<Object> response = userManagementService.getManageUserList(dto);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return (ResponseEntity) ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
        } catch (Exception e) {
            log.error("역할별 사용자 목록 조회 중 오류 발생", e);
            return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/search_userList")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchuserList(@RequestBody Map<String, Object> map) {
        try {
            if (map != null && map.get("userId") != null) {
                String sanitizedUserId = inputSanitizer.sanitizeUserId(map.get("userId").toString());
                map.put("userId", sanitizedUserId);
            }
            if (map != null && map.get("searchKeyword") != null) {
                String searchKeyword = map.get("searchKeyword").toString();
                if (inputSanitizer.containsSqlInjection(searchKeyword) || 
                    inputSanitizer.containsXss(searchKeyword)) {
                    return (ResponseEntity) ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
                }
                map.put("searchKeyword", inputSanitizer.sanitize(searchKeyword));
            }
            ServiceResponse<List<Map<String, Object>>> response = userSearchService.searchUserList(map);
            return convertToApiResponse(response);
        } catch (Exception e) {
            log.error("사용자 검색 중 오류 발생", e);
            return ApiResponse.<List<Map<String, Object>>>error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<ApiResponse<T>> convertToApiResponse(ServiceResponse<T> serviceResponse) {
        if (serviceResponse.isSuccess()) {
            return (ResponseEntity<ApiResponse<T>>) ApiResponse.ok(serviceResponse.getData());
        } else {
            ApiResultCode errorCode = ApiResultCode.UNKNOWN_ERR;
            if ("400".equals(serviceResponse.getResultCode())) {
                errorCode = ApiResultCode.PARAM_VALID_ERR;
            } else if ("500".equals(serviceResponse.getResultCode())) {
                errorCode = ApiResultCode.UNKNOWN_ERR;
            }
            return ApiResponse.<T>error(errorCode, serviceResponse.getMessage());
        }
    }
}