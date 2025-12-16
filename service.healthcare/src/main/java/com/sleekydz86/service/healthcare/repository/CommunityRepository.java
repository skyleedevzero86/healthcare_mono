package com.sleekydz86.service.healthcare.repository;

import java.util.List;
import java.util.Map;

public interface CommunityRepository {
    int savePost(Map<String, Object> params);
    List<Map<String, Object>> findPostList(Map<String, Object> params);
}

