package com.sleekydz86.service.usermanagement.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_USER_INFO = "userInfo";
    public static final String CACHE_USER_LIST = "userList";
    public static final String CACHE_DOCTOR_LIST = "doctorList";
    public static final String CACHE_PARENT_LIST = "parentList";
    public static final String CACHE_HEALTH_DATA = "healthData";
    public static final String CACHE_COMMUNITY = "community";
    
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    public static final Duration USER_INFO_TTL = Duration.ofHours(2);
    public static final Duration USER_LIST_TTL = Duration.ofMinutes(10);
    public static final Duration DOCTOR_LIST_TTL = Duration.ofMinutes(15);
    public static final Duration PARENT_LIST_TTL = Duration.ofMinutes(15);
    public static final Duration HEALTH_DATA_TTL = Duration.ofMinutes(10);
    public static final Duration COMMUNITY_TTL = Duration.ofHours(1);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CACHE_USER_INFO, defaultConfig.entryTtl(USER_INFO_TTL));
        cacheConfigurations.put(CACHE_USER_LIST, defaultConfig.entryTtl(USER_LIST_TTL));
        cacheConfigurations.put(CACHE_DOCTOR_LIST, defaultConfig.entryTtl(DOCTOR_LIST_TTL));
        cacheConfigurations.put(CACHE_PARENT_LIST, defaultConfig.entryTtl(PARENT_LIST_TTL));
        cacheConfigurations.put(CACHE_HEALTH_DATA, defaultConfig.entryTtl(HEALTH_DATA_TTL));
        cacheConfigurations.put(CACHE_COMMUNITY, defaultConfig.entryTtl(COMMUNITY_TTL));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

