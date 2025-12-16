package com.sleekydz86.service.usermanagement.service.health;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.repository.UserHealthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHealthServiceImpl implements UserHealthService {
    private final UserHealthRepository userHealthRepository;

    @Override
    @Cacheable(value = "healthData", key = "'ageavg_' + #dto.ageRange + '_' + #dto.gender")
    public ServiceResponse<Map<String, Object>> getAgeAvgHealthInfo(UserhealthDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserhealthDto cannot be null");
            }
            Map<String, Object> result = userHealthRepository.findAgeAvgHealthInfo(dto);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("Age avg health info query failed: " + e.getMessage());
        }
    }
}

