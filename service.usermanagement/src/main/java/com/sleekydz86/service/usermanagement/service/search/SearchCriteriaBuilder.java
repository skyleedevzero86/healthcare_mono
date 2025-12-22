package com.sleekydz86.service.usermanagement.service.search;

import com.sleekydz86.service.usermanagement.dto.UserDto;

import java.util.Map;

public interface SearchCriteriaBuilder {
    UserDto buildSearchCriteria(Map<String, Object> params);
}

