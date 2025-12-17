package com.sleekydz86.service.usermanagement.service.cache;

import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Cacheable(value = "userInfo", key = "#id", unless = "#result == null")
    public User getUser(Long id) {
        User user = getCachedUser(id);
        if (user != null) {
            return user;
        }
        Optional<User> optionalUser = userJpaRepository.findById(id);
        user = optionalUser.orElse(null);
        if (user != null) {
            cacheUserData(user);
        }
        return user;
    }

    public void cacheUserData(User user) {
        String key = "user:" + user.getId();
        redisTemplate.opsForValue().set(key, user, Duration.ofHours(24));
    }

    public User getCachedUser(Long id) {
        String key = "user:" + id;
        return (User) redisTemplate.opsForValue().get(key);
    }

    @CacheEvict(value = "userInfo", key = "#id")
    public void removeUserFromCache(Long id) {
        String key = "user:" + id;
        redisTemplate.delete(key);
    }
}

