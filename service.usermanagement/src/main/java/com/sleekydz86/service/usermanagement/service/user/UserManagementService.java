package com.sleekydz86.service.usermanagement.service.user;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.entity.User;

import java.util.Map;

public interface UserManagementService {
    ServiceResponse<Object> getUserList(UserDto dto);
    ServiceResponse<Object> getParentList(UserDto dto);
    ServiceResponse<Object> getDoctorList(UserDto dto);
    ServiceResponse<Object> getManageUserList(UserDto dto);
    ServiceResponse<Map<String, Object>> getUserInfo(UserDto dto);
    ServiceResponse<Integer> updateUserInfo(UserDto dto);
    ServiceResponse<Integer> deleteUserInfo(UserDto dto);
    ServiceResponse<Integer> updatePassword(UserDto dto);
    User createUser(User user);
    User getUser(Long id);
    User updateUser(User user);
    void deleteUser(Long id);
}

