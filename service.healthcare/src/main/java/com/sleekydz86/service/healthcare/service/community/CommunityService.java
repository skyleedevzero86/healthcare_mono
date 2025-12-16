package com.sleekydz86.service.healthcare.service.community;

import com.sleekydz86.service.healthcare.common.ServiceResponse;

import java.util.List;
import java.util.Map;

public interface CommunityService {
    ServiceResponse<Integer> createPost(Map<String, Object> params);
    ServiceResponse<List<CommunityPost>> getPostList(Map<String, Object> params);
}

