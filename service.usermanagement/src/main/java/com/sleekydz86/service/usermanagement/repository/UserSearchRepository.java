package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserSearchRepository {
    List<Map<String, Object>> searchUserList(Map<String, Object> map);
    List<Map<String, Object>> searchDoctor(UserDto dto);
    List<Map<String, Object>> searchParent(UserDto dto);
    List<Map<String, Object>> searchHealthUserList(UserDto dto);
    List<Map<String, Object>> searchDoctorGuardianList(UserDto dto);
}

