package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserSearchRepositoryImpl implements UserSearchRepository {
    private final UserMapper userMapper;

    public UserSearchRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<Map<String, Object>> searchUserList(Map<String, Object> map) {
        return userMapper.searchuserList(map);
    }

    @Override
    public List<Map<String, Object>> searchDoctor(UserDto dto) {
        return userMapper.searchDoctor(dto);
    }

    @Override
    public List<Map<String, Object>> searchParent(UserDto dto) {
        return userMapper.searchParent(dto);
    }

    @Override
    public List<Map<String, Object>> searchHealthUserList(UserDto dto) {
        return userMapper.searchHealthUserList(dto);
    }

    @Override
    public List<Map<String, Object>> searchDoctorGuardianList(UserDto dto) {
        List<Map<String, Object>> doctorList = userMapper.searchdoctorList(dto);
        List<Map<String, Object>> guardianList = userMapper.searchguardianList(dto);

        List<Map<String, Object>> result = new ArrayList<>();
        if (doctorList != null && !doctorList.isEmpty()) {
            result.addAll(doctorList);
        }

        if (guardianList != null && !guardianList.isEmpty()) {
            result.addAll(guardianList);
        }

        return result;
    }
}

