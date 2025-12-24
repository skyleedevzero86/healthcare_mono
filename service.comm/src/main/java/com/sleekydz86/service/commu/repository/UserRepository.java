package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Usermng;
import com.sleekydz86.service.commu.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserMapper userMapper;

    public Usermng findOne(int userId) {
        return userMapper.findOne(userId);
    }
}