package com.sleekydz86.service.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String TOKEN_USAGE_PREFIX = "token:usage:";

    public void addToBlacklist(String token, long expirationTime) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
            log.info("토큰 블랙리스트 추가 완료");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 실패", e);
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("블랙리스트 확인 실패", e);
            return false;
        }
    }

    public void recordTokenUsage(String token, String userId, long ttl) {
        try {
            String key = TOKEN_USAGE_PREFIX + token;
            redisTemplate.opsForValue().set(key, userId, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("토큰 사용 기록 실패", e);
        }
    }

    public boolean isTokenUsed(String token) {
        try {
            String key = TOKEN_USAGE_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("토큰 사용 확인 실패", e);
            return false;
        }
    }
}

