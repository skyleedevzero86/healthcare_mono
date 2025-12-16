package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserhealthDto;

import java.util.Map;

public interface UserHealthRepository {
    Map<String, Object> findAgeAvgHealthInfo(UserhealthDto dto);
}

