package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CommunityRepositoryImpl implements CommunityRepository {
    private final HealthcareMapper healthcareMapper;

    public CommunityRepositoryImpl(HealthcareMapper healthcareMapper) {
        this.healthcareMapper = healthcareMapper;
    }

    @Override
    public int savePost(Map<String, Object> params) {
        return healthcareMapper.inscommunity(params);
    }

    @Override
    public List<Map<String, Object>> findPostList(Map<String, Object> params) {
        return healthcareMapper.commulist(params);
    }
}

