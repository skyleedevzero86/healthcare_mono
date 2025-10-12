package com.sleekydz86.service.usermanagement.service;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface UserService {
    public Object userList(UserDto dto);

    public Object parentList(UserDto dto);

    public Object doctorList(UserDto dto);

    public Object manage_userList(UserDto dto);

    public List<Map<String, Object>> searchuserList(Map<String, Object> map);

    @SuppressWarnings("unchecked")
    public Map<String, Object> userInfo(UserDto dto) throws Exception;

    public int updateUserInfo(@Valid UserDto dto);

    public int deleteUserInfo(@Valid UserDto dto);

    public int updatePasswd(@Valid UserDto dto);

    public List<Map<String, Object>> searchDoctor(UserDto dto);

    public List<Map<String, Object>> searchParent(UserDto dto);

    public List<Map<String, Object>> searchHealthUserList(Map<String, Object> map);

    public List<Map<String, Object>> searchdrguardianList(UserDto dto);

    public Map<String, Object> ageavgHealthinfo(UserhealthDto dto);
}