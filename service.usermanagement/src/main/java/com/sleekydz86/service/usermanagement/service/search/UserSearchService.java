package com.sleekydz86.service.usermanagement.service.search;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserSearchService {
    ServiceResponse<List<Map<String, Object>>> searchUserList(Map<String, Object> map);
    ServiceResponse<List<Map<String, Object>>> searchDoctor(UserDto dto);
    ServiceResponse<List<Map<String, Object>>> searchParent(UserDto dto);
    ServiceResponse<List<Map<String, Object>>> searchHealthUserList(Map<String, Object> map);
    ServiceResponse<List<Map<String, Object>>> searchDoctorGuardianList(UserDto dto);
}

