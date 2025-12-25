package com.sleekydz86.service.usermanagement.service.cache;

import com.sleekydz86.service.usermanagement.config.CacheConfig;
import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String USER_KEY_PREFIX = "user:";
    private static final Duration USER_DIRECT_TTL = CacheConfig.USER_INFO_TTL;

    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("mapperUserMapper")
    private final UserMapper userMapper;

    @Cacheable(value = CacheConfig.CACHE_USER_INFO, key = "#id", unless = "#result == null")
    public User getUser(Long id) {
        User user = getCachedUser(id);
        if (user != null) {
            return user;
        }
        user = userMapper.findById(id);
        if (user != null) {
            cacheUserData(user);
        }
        return user;
    }

    public void cacheUserData(User user) {
        String key = USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, user, USER_DIRECT_TTL);
    }

    public User getCachedUser(Long id) {
        String key = USER_KEY_PREFIX + id;
        return (User) redisTemplate.opsForValue().get(key);
    }

    @CacheEvict(value = CacheConfig.CACHE_USER_INFO, key = "#id")
    public void removeUserFromCache(Long id) {
        evictUserDirectCache(id);
    }

    public void evictUserDirectCache(Long id) {
        String key = USER_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }
}

