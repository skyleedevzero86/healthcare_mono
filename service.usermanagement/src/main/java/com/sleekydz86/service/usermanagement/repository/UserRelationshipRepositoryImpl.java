package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRelationshipRepositoryImpl implements UserRelationshipRepository {
    private final UserMapper userMapper;

    public UserRelationshipRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int updateGuardianMapping(UserDto dto) {
        return userMapper.updateGuardianMaping(dto);
    }

    @Override
    public int updateDoctorMapping(UserDto dto) {
        return userMapper.updateDoctorMaping(dto);
    }

    @Override
    public int countSearchParent(UserDto dto) {
        return userMapper.searchParentCount(dto);
    }

    @Override
    public int deleteParentMapping(UserDto dto) {
        return userMapper.deleteParentMapping(dto);
    }
}

