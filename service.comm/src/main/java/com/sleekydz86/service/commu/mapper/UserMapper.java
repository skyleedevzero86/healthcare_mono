package com.sleekydz86.service.commu.mapper;

import com.sleekydz86.service.commu.entity.Usermng;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    Usermng findOne(@Param("userId") int userId);
}

