package com.sleekydz86.service.usermanagement.mapper;

import com.sleekydz86.service.usermanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User findByUsername(String username);
    int insert(User user);
    int update(User user);
    int deleteById(Long id);
}

