package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.service.community.CommunityPost;

import java.util.List;
import java.util.Map;

public interface CommunityRepository {
    int savePost(Map<String, Object> params);
    List<CommunityPost> findPostList(Map<String, Object> params);
}

