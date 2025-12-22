package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserHealthRepositoryImpl implements UserHealthRepository {
    private final UserMapper userMapper;

    public UserHealthRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, Object> findAgeAvgHealthInfo(UserhealthDto dto) {
        return userMapper.ageavgHealthinfo(dto);
    }
}

