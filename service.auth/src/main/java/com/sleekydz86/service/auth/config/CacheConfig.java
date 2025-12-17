package com.sleekydz86.service.auth.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    public static final String CACHE_ENCRYPTION = "encryptionCache";
    public static final String CACHE_DECRYPTION = "decryptionCache";
    public static final String CACHE_TOKEN_BLACKLIST = "tokenBlacklist";
    
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    public static final Duration USER_INFO_TTL = Duration.ofHours(2);
    public static final Duration ENCRYPTION_TTL = Duration.ofHours(24);
    public static final Duration TOKEN_BLACKLIST_TTL = Duration.ofDays(7);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CACHE_USER_INFO, defaultConfig.entryTtl(USER_INFO_TTL));
        cacheConfigurations.put(CACHE_ENCRYPTION, defaultConfig.entryTtl(ENCRYPTION_TTL));
        cacheConfigurations.put(CACHE_DECRYPTION, defaultConfig.entryTtl(ENCRYPTION_TTL));
        cacheConfigurations.put(CACHE_TOKEN_BLACKLIST, defaultConfig.entryTtl(TOKEN_BLACKLIST_TTL));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}


