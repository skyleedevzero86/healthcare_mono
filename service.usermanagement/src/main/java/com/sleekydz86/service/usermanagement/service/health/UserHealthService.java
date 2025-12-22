package com.sleekydz86.service.usermanagement.service.health;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;

import java.util.Map;

public interface UserHealthService {
    ServiceResponse<Map<String, Object>> getAgeAvgHealthInfo(UserhealthDto dto);
}

