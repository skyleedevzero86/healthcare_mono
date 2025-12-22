package com.sleekydz86.service.healthcare.service.community;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.CommunityRepository;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final HealthDataValidator healthDataValidator;
    private final AuthServiceClient authServiceClient;
    private final HealthcareMetrics healthcareMetrics;

    @Override
    @Transactional
    @CacheEvict(value = "community", allEntries = true)
    public ServiceResponse<Integer> createPost(Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String userId = (String) params.get("userId");
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("requestId", requestId);
        MDC.put("operation", "inscommunity");

        try {
            healthDataValidator.validate(params);
            log.info("커뮤니티 게시글 생성 중: {}", userId);

            if (params.get("userSeq") == null) {
                Map<String, String> request = new HashMap<>();
                request.put("userId", userId);
                Map<String, Object> response = authServiceClient.getUserSeq(request);
                if (response != null && response.get("resultCode") != null
                        && "0000".equals(response.get("resultCode"))) {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data != null && data.get("userSeq") != null) {
                        params.put("userSeq", ((Number) data.get("userSeq")).intValue());
                    }
                }
            }
            int result = communityRepository.savePost(params);

            if (result > 0) {
                healthcareMetrics.incrementCommunityPostCreated();
            }

            log.info("커뮤니티 게시글 생성 완료: 사용자 {}, 결과: {}", userId, result);
            return ServiceResponse.success(result);
        } catch (ValidationException e) {
            return ServiceResponse.error("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("커뮤니티 게시글 생성 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("커뮤니티 게시글 생성 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "community", key = "#params['pageIdx'] + '_' + (#params['searchKeyword'] != null ? #params['searchKeyword'] : '')")
    public ServiceResponse<List<CommunityPost>> getPostList(Map<String, Object> params) {
        try {
            List<CommunityPost> result = communityRepository.findPostList(params);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("커뮤니티 게시글 목록 조회 중 오류 발생", e);
            return ServiceResponse.error("커뮤니티 게시글 목록 조회 실패: " + e.getMessage());
        }
    }
}

