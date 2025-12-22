package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<Map<String, Object>> findUserList(UserDto dto) {
        return userMapper.userList(dto);
    }

    @Override
    public int countUserList(UserDto dto) {
        return userMapper.userListCount(dto);
    }

    @Override
    public List<Map<String, Object>> findParentList(UserDto dto) {
        return userMapper.parentList(dto);
    }

    @Override
    public int countParentList(UserDto dto) {
        return userMapper.parentListCount(dto);
    }

    @Override
    public List<Map<String, Object>> findDoctorList(UserDto dto) {
        return userMapper.doctorList(dto);
    }

    @Override
    public int countDoctorList(UserDto dto) {
        return userMapper.doctorListCount(dto);
    }

    @Override
    public List<Map<String, Object>> findManageUserList(UserDto dto) {
        return userMapper.manage_userList(dto);
    }

    @Override
    public int countManageUserList(UserDto dto) {
        return userMapper.manage_userList_cnt(dto);
    }

    @Override
    public Map<String, Object> findUserInfo(UserDto dto) {
        return userMapper.userInfo(dto);
    }

    @Override
    public int updateUserInfo(UserDto dto) {
        return userMapper.updateUserInfo(dto);
    }

    @Override
    public int deleteUserInfo(UserDto dto) {
        return userMapper.deleteUserInfo(dto);
    }

    @Override
    public int updatePassword(UserDto dto) {
        return userMapper.updatePasswd(dto);
    }
}

