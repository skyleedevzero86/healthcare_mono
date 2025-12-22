package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    List<Map<String, Object>> findUserList(UserDto dto);
    int countUserList(UserDto dto);
    List<Map<String, Object>> findParentList(UserDto dto);
    int countParentList(UserDto dto);
    List<Map<String, Object>> findDoctorList(UserDto dto);
    int countDoctorList(UserDto dto);
    List<Map<String, Object>> findManageUserList(UserDto dto);
    int countManageUserList(UserDto dto);
    Map<String, Object> findUserInfo(UserDto dto);
    int updateUserInfo(UserDto dto);
    int deleteUserInfo(UserDto dto);
    int updatePassword(UserDto dto);
}

