package com.sleekydz86.service.llm.infrastructure.adapter.cache;

import com.sleekydz86.service.llm.config.CacheProperties;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.ports.outbound.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCacheRepository implements CacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;
    private static final String CACHE_KEY_PREFIX = "llm:cache:";

    @Override
    public LLMGenerationResult get(Prompt prompt) {
        if (!cacheProperties.isEnabled()) {
            return null;
        }

        try {
            String cacheKey = generateCacheKey(prompt.getFinalContent());
            Object cached = redisTemplate.opsForValue().get(cacheKey);

            if (cached instanceof LLMGenerationResult) {
                log.debug("캐시 히트: key={}", cacheKey);
                return (LLMGenerationResult) cached;
            }

            return null;
        } catch (Exception e) {
            log.warn("캐시 조회 오류", e);
            return null;
        }
    }

    @Override
    public void save(Prompt prompt, LLMGenerationResult result) {
        if (!cacheProperties.isEnabled()) {
            return;
        }

        try {
            String cacheKey = generateCacheKey(prompt.getFinalContent());
            int ttl = cacheProperties.getTtl();

            redisTemplate.opsForValue().set(
                    cacheKey,
                    result,
                    ttl,
                    TimeUnit.SECONDS);

            log.debug("캐시 저장: key={}, ttl={}s", cacheKey, ttl);
        } catch (Exception e) {
            log.warn("캐시 저장 오류", e);
        }
    }

    @Override
    public void evict(Prompt prompt) {
        try {
            String cacheKey = generateCacheKey(prompt.getFinalContent());
            redisTemplate.delete(cacheKey);
            log.debug("캐시 삭제: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("캐시 삭제 오류", e);
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
}

