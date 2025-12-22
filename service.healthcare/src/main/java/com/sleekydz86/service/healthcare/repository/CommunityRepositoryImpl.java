package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import com.sleekydz86.service.healthcare.service.community.CommunityPost;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<CommunityPost> findPostList(Map<String, Object> params) {
        List<Map<String, Object>> rawList = healthcareMapper.commulist(params);
        return rawList.stream().map(map -> {
            CommunityPost post = new CommunityPost();
            if (map.get("postSeq") != null) {
                post.setPostSeq(((Number) map.get("postSeq")).intValue());
            }
            if (map.get("userId") != null) {
                post.setUserId(map.get("userId").toString());
            }
            if (map.get("userSeq") != null) {
                post.setUserSeq(((Number) map.get("userSeq")).intValue());
            }
            if (map.get("title") != null) {
                post.setTitle(map.get("title").toString());
            }
            if (map.get("content") != null) {
                post.setContent(map.get("content").toString());
            }
            if (map.get("createdAt") != null) {
                if (map.get("createdAt") instanceof LocalDateTime) {
                    post.setCreatedAt((LocalDateTime) map.get("createdAt"));
                }
            }
            if (map.get("updatedAt") != null) {
                if (map.get("updatedAt") instanceof LocalDateTime) {
                    post.setUpdatedAt((LocalDateTime) map.get("updatedAt"));
                }
            }
            if (map.get("viewCount") != null) {
                post.setViewCount(((Number) map.get("viewCount")).intValue());
            }
            if (map.get("searchKeyword") != null) {
                post.setSearchKeyword(map.get("searchKeyword").toString());
            }
            return post;
        }).collect(Collectors.toList());
    }
}

