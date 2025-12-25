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
    private static final String TOKEN_REUSE_PREFIX = "token:reuse:";
    private static final long MAX_TOKEN_LENGTH = 2048;

    public void addToBlacklist(String token, long expirationTime) {
        if (token == null || token.length() > MAX_TOKEN_LENGTH) {
            log.warn("유효하지 않은 토큰을 블랙리스트에 추가 시도");
            return;
        }
        
        try {
            String key = BLACKLIST_PREFIX + token.hashCode();
            redisTemplate.opsForValue().set(key, token, expirationTime, TimeUnit.MILLISECONDS);
            
            String fullKey = BLACKLIST_PREFIX + "full:" + token;
            redisTemplate.opsForValue().set(fullKey, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 실패", e);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.length() > MAX_TOKEN_LENGTH) {
            return false;
        }
        
        try {
            String key = BLACKLIST_PREFIX + token.hashCode();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                String storedToken = redisTemplate.opsForValue().get(key);
                if (token.equals(storedToken)) {
                    return true;
                }
            }
            
            String fullKey = BLACKLIST_PREFIX + "full:" + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(fullKey));
        } catch (Exception e) {
            log.error("블랙리스트 확인 실패", e);
            return false;
        }
    }

    public void recordTokenUsage(String token, String userId, long ttl) {
        if (token == null || userId == null) {
            return;
        }
        
        try {
            String key = TOKEN_USAGE_PREFIX + token.hashCode();
            String value = userId + ":" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("토큰 사용 기록 실패", e);
        }
    }

    public boolean isTokenUsed(String token) {
        if (token == null) {
            return false;
        }
        
        try {
            String key = TOKEN_USAGE_PREFIX + token.hashCode();
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("토큰 사용 확인 실패", e);
            return false;
        }
    }

    public boolean checkTokenReuse(String token, String userId) {
        if (token == null || userId == null) {
            return false;
        }
        
        try {
            String key = TOKEN_REUSE_PREFIX + token.hashCode();
            String stored = redisTemplate.opsForValue().get(key);
            
            if (stored != null && !stored.equals(userId)) {
                log.warn("토큰 재사용 시도 감지: token={}, expected={}, actual={}", 
                    token.substring(0, Math.min(10, token.length())), stored, userId);
                return true;
            }
            
            redisTemplate.opsForValue().set(key, userId, 5, TimeUnit.MINUTES);
            return false;
        } catch (Exception e) {
            log.error("토큰 재사용 확인 실패", e);
            return false;
        }
    }
}

