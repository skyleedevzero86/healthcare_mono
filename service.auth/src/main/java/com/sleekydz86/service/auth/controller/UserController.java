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

    @PostMapping("/v1/signup")
    @Transactional
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupDto user) throws Exception {
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
            return ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.INSERT_FAIL);
        }
    }

    @PostMapping("/v1/duplicateId")
    public ResponseEntity<ApiResponse> duplicateId(@RequestBody UserDto dto) throws Exception {
        if (userService.duplicateId(dto)) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.DUPLICATE_CODE);
        }
    }

    @PostMapping("/v1/duplicateEmail")
    public ResponseEntity<ApiResponse> duplicateEmail(@RequestBody UserDto dto) throws Exception {
        if (userService.duplicateEmail(dto)) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.DUPLICATE_CODE);
        }
    }

    @PostMapping("/v1/searchDoctor")
    public ResponseEntity<ApiResponse> serchDoctor(@RequestBody UserDto dto) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = userService.searchDoctor(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
    }

    @PostMapping("/v1/searchParent")
    public ResponseEntity<ApiResponse> serchParent(@RequestBody UserDto dto) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = userService.searchParent(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
    }

    @PostMapping("/v1/signin")
    public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SigninDto user) throws Exception {
        return ApiResponse.ok(userService.signin(user));
    }

    @PostMapping("/v1/findUserId")
    public ResponseEntity<ApiResponse> findUserId(@RequestBody FindDto dto) throws Exception {
        Map<String, Object> map = userService.findUserId(dto);
        return ApiResponse.ok(map);
    }

    @PostMapping("/v1/findUserPw")
    public ResponseEntity<ApiResponse> findUserPw(@RequestBody FindDto dto) throws Exception {
        Map<String, Object> map = userService.findUserPw(dto);
        if (!map.isEmpty()) {
            if ((Long) map.get("count") != 0) {
                return ApiResponse.ok();
            } else {
                return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
            }
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/updateUserPw")
    public ResponseEntity<ApiResponse> updateUserPw(@RequestBody FindDto dto) throws Exception {
        int result = userService.updateUserPw(dto);
        if (result > 0) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        }
    }

    @PostMapping("/v1/refresh")
    public ResponseEntity<ApiResponse> refresh(HttpServletRequest req) throws Exception {
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
        return ApiResponse.ok(token);
    }

    @PostMapping("/v1/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest req) throws Exception {
        String token = "";
        String bearerToken = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            token = bearerToken.substring(7);
        }

        userService.logout(token);
        return ApiResponse.ok();
    }

}