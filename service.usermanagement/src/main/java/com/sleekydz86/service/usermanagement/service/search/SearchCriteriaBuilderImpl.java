package com.sleekydz86.service.usermanagement.service.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SearchCriteriaBuilderImpl implements SearchCriteriaBuilder {
    private final ObjectMapper objectMapper;

    public SearchCriteriaBuilderImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDto buildSearchCriteria(Map<String, Object> params) {
        return objectMapper.convertValue(params, UserDto.class);
    }
}

