package com.sleekydz86.service.auth.controller;

import com.sleekydz86.service.auth.dto.*;
import com.sleekydz86.service.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final com.sleekydz86.service.auth.util.InputSanitizer inputSanitizer;

    @PostMapping("/v1/signup")
    @Transactional
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupDto user) {
        try {
            String sanitizedUserId = inputSanitizer.sanitizeUserId(user.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(user.getUserId())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            user.setUserId(sanitizedUserId);
            
            if (!inputSanitizer.isValidEmail(user.getEmail())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            if (inputSanitizer.containsSqlInjection(user.getUserNm()) || 
                inputSanitizer.containsXss(user.getUserNm())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
        int result = userService.signup(user);
        if (result == 1) {
            userService.insUserAuth(user);
            if (user.getDoctorSeq() != 0) {
                userService.insDoctorMapping(user);
            }
            if (user.getGuardian() != null) {
                if (!user.getGuardian().isEmpty()) {
                    JSONArray arr = new JSONArray(user.getGuardian());
                    for (Object obj : arr) {
                        JSONObject json = new JSONObject(obj.toString());
                        log.info(json.toString());
                        log.info(json.keys().toString());
                        user.setGuardianSeq((int) json.get("guardianSeq"));
                        user.setGuardianId((String) json.get("guardianId"));
                        userService.insGuardianMapping(user);
                    }
                }
            }
            return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.INSERT_FAIL);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/duplicateId")
    public ResponseEntity<ApiResponse<?>> duplicateId(@RequestBody UserDto dto) {
        try {
            if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            String sanitizedUserId = inputSanitizer.sanitizeUserId(dto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(dto.getUserId())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            dto.setUserId(sanitizedUserId);
            
            if (userService.duplicateId(dto)) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
                return ResponseEntity.ok(responseEntity.getBody());
        } else {
                return ApiResponse.error(ApiResultCode.DUPLICATE_CODE);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            log.error("ID 중복 확인 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/duplicateEmail")
    public ResponseEntity<ApiResponse<?>> duplicateEmail(@RequestBody UserDto dto) {
        try {
            if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            if (!inputSanitizer.isValidEmail(dto.getEmail())) {
                return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
            }
            
            if (userService.duplicateEmail(dto)) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
                return ResponseEntity.ok(responseEntity.getBody());
        } else {
                return ApiResponse.error(ApiResultCode.DUPLICATE_CODE);
        }
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            log.error("이메일 중복 확인 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/searchDoctor")
    public ResponseEntity<ApiResponse<?>> serchDoctor(@RequestBody UserDto dto) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = userService.searchDoctor(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok(result);
        }
    }

    @PostMapping("/v1/searchParent")
    public ResponseEntity<ApiResponse<?>> serchParent(@RequestBody UserDto dto) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = userService.searchParent(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok(result);
        }
    }

    @PostMapping("/v1/signin")
    public ResponseEntity<ApiResponse<?>> signin(@Valid @RequestBody SigninDto user) {
        Map<Object, Object> result = userService.signin(user);
        return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok((Map<Object, Object>) result);
    }

    @PostMapping("/v1/findUserId")
    public ResponseEntity<ApiResponse<?>> findUserId(@RequestBody FindDto dto) {
        Map<String, Object> map = userService.findUserId(dto);
        return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok(map);
    }

    @PostMapping("/v1/findUserPw")
    public ResponseEntity<ApiResponse<?>> findUserPw(@RequestBody FindDto dto) {
        Map<String, Object> map = userService.findUserPw(dto);
        if (!map.isEmpty()) {
            if ((Long) map.get("count") != 0) {
                ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
                return ResponseEntity.ok(responseEntity.getBody());
            } else {
                    return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
            }
        } else {
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/v1/updateUserPw")
    public ResponseEntity<ApiResponse<?>> updateUserPw(@RequestBody FindDto dto) {
        int result = userService.updateUserPw(dto);
        if (result > 0) {
            return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        }
    }

    @PostMapping("/v1/getUserSeq")
    public ResponseEntity<ApiResponse<?>> getUserSeq(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.isEmpty()) {
            ResponseEntity<ApiResponse<Map<String, Object>>> responseEntity = ApiResponse.<Map<String, Object>>error(ApiResultCode.PARAM_VALID_ERR);
            return ResponseEntity.ok(responseEntity.getBody());
        }
        Integer userSeq = userService.getUserSeq(userId);
        if (userSeq == null) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userSeq", userSeq);
        ResponseEntity<ApiResponse<Map<String, Object>>> responseEntity = ApiResponse.ok(result);
        return ResponseEntity.ok(responseEntity.getBody());
    }

    @PostMapping("/v1/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest req) {
        String accessToken = "";
        String bearerToken = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            accessToken = bearerToken.substring(7);
        }
        String refreshToken = "";
        String refreshBearerToken = req.getHeader("refreshToken");
        if (StringUtils.hasText(refreshBearerToken) && refreshBearerToken.startsWith("Bearer")) {
            refreshToken = refreshBearerToken.substring(7);
        }

        JwtTokenDto token = userService.refresh(accessToken, refreshToken);
        return (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) ApiResponse.ok(token);
    }

    @PostMapping("/v1/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest req) {
        String token = "";
        String bearerToken = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            token = bearerToken.substring(7);
        }

        userService.logout(token);
        ResponseEntity<ApiResponse<Void>> responseEntity = ApiResponse.<Void>ok();
        return ResponseEntity.ok(responseEntity.getBody());
    }

}