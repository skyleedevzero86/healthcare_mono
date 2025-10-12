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

    @PostMapping("/v1/userInfo")
    public ResponseEntity<ApiResponse> userInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        return ApiResponse.ok(userService.userInfo(dto));
    }

    @PostMapping("/v1/userBoardInfo")
    public ResponseEntity<ApiResponse> userBoardInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {

        Map<String, Object> responseData = new HashMap<>();

        log.info("ash userboardinfo : " + dto.toString());
        Map<String, Object> userBioinfo = userService.userInfo(dto);
        List<Map<String, Object>> userRoleinfo = userService.searchdrguardianList(dto);

        UserhealthDto userhealthDto = new UserhealthDto();
        userhealthDto.setBirthdate(AES256Util.decrypt((String) userBioinfo.get("birthEnc")));
        userhealthDto.setGender((String) userBioinfo.get("gender"));

        Map<String, Object> userHealthavg = userService.ageavgHealthinfo(userhealthDto);

        responseData.put("userBioinfo", userBioinfo);
        responseData.put("userRolelist", userRoleinfo);
        responseData.put("userHealthavg", userHealthavg);

        log.info("ash userboardinfo result : " + responseData.toString());

        if (userBioinfo.isEmpty()) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            return ApiResponse.ok(responseData);
        }
    }

    @PostMapping("/v1/updateUserInfo")
    public ResponseEntity<ApiResponse> updateUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        int result = userService.updateUserInfo(dto);

        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/deleteUserInfo")
    public ResponseEntity<ApiResponse> deleteUserInfo(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        int result = userService.deleteUserInfo(dto);
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/updatePasswd")
    public ResponseEntity<ApiResponse> updatePasswd(HttpServletRequest req, @Valid @RequestBody UserDto dto)
            throws Exception {
        int result = userService.updatePasswd(dto);
        if (result == 1) {
            return ApiResponse.ok();
        } else if (result == 0) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/searchDoctor")
    public ResponseEntity<ApiResponse> searchDoctor(@RequestBody UserDto dto) {
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
    public ResponseEntity<ApiResponse> searchParent(@RequestBody UserDto dto) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = userService.searchParent(dto);
        if (list.size() == 0) {
            return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
        } else {
            result.put("list", list);
            return ApiResponse.ok(result);
        }
    }

    @RequestMapping("/v1/list")
    public ResponseEntity<ApiResponse> userList(UserDto dto, @RequestBody Map<String, Object> map) throws Exception {

        dto.setUserRoleFk((String) map.get("userRoleFk"));
        dto.setPageIndex((int) map.get("pageIdx"));
        dto.setPageOffset((int) map.get("pageOffset"));
        dto.setSearchKeyword((String) map.get("searchKeyword"));

        if (map.get("userRoleFk").equals("3")) {
            return ApiResponse.ok(userService.doctorList(dto));
        } else if (map.get("userRoleFk").equals("2")) {
            return ApiResponse.ok(userService.parentList(dto));
        } else if (map.get("userRoleFk").equals("1")) {
            dto.setUserId((String) map.getOrDefault("userId", null));
            return ApiResponse.ok(userService.userList(dto));
        } else {
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/v1/healthUserList")
    public ResponseEntity<ApiResponse> searchHealthUserList(@RequestBody Map<String, Object> map) {
        return ApiResponse.ok(userService.searchHealthUserList(map));
    }

    @PostMapping("/v1/drguardianList")
    public ResponseEntity<ApiResponse> drguardianList(UserDto dto) {
        return ApiResponse.ok(userService.searchdrguardianList(dto));
    }

    @PostMapping("/v1/manage_userList")
    public ResponseEntity<ApiResponse> roleuserList(@RequestBody Map<String, Object> map) {
        UserDto dto = new UserDto();
        dto.setUserId((String) map.get("userId"));
        dto.setUserRoleFk((String) map.get("userRoleFk"));
        dto.setPageIndex((int) map.get("pageIdx"));
        dto.setPageOffset((int) map.get("pageOffset"));
        dto.setSearchKeyword((String) map.get("searchKeyword"));

        return ApiResponse.ok(userService.manage_userList(dto));
    }

    @PostMapping("/v1/search_userList")
    public ResponseEntity<ApiResponse> searchuserList(@RequestBody Map<String, Object> map) {
        return ApiResponse.ok(userService.searchuserList(map));
    }
}