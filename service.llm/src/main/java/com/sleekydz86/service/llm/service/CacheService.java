package com.sleekydz86.service.llm.service;

import com.sleekydz86.service.llm.config.LLMProperties;
import com.sleekydz86.service.llm.dto.LLMResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final LLMProperties llmProperties;
    private static final String CACHE_KEY_PREFIX = "llm:cache:";

    public LLMResponse getCachedResponse(String prompt) {
        if (!llmProperties.getCache().isEnabled()) {
            return null;
        }

        try {
            String cacheKey = generateCacheKey(prompt);
            Object cached = redisTemplate.opsForValue().get(cacheKey);

            if (cached instanceof LLMResponse) {
                log.debug("캐시 히트: key={}", cacheKey);
                return (LLMResponse) cached;
            }

            return null;
        } catch (Exception e) {
            log.warn("캐시 조회 오류", e);
            return null;
        }
    }

    public void cacheResponse(String prompt, LLMResponse response) {
        if (!llmProperties.getCache().isEnabled()) {
            return;
        }

        try {
            String cacheKey = generateCacheKey(prompt);
            int ttl = llmProperties.getCache().getTtl();

            redisTemplate.opsForValue().set(
                    cacheKey,
                    response,
                    ttl,
                    TimeUnit.SECONDS);

            log.debug("캐시 저장: key={}, ttl={}s", cacheKey, ttl);
        } catch (Exception e) {
            log.warn("캐시 저장 오류", e);
        }
    }

    private String generateCacheKey(String prompt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(prompt.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return CACHE_KEY_PREFIX + hexString.toString();
        } catch (Exception e) {
            log.error("캐시 키 생성 오류", e);
            return CACHE_KEY_PREFIX + String.valueOf(prompt.hashCode());
        }
    }

    public void evictCache(String prompt) {
        try {
            String cacheKey = generateCacheKey(prompt);
            redisTemplate.delete(cacheKey);
            log.debug("캐시 삭제: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("캐시 삭제 오류", e);
        }
    }
}
