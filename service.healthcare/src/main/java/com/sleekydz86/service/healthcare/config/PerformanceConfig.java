package com.sleekydz86.service.healthcare.config;

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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableCaching
@EnableAsync
public class PerformanceConfig {

    public static final String CACHE_HEALTH_SCORE = "healthScore";
    public static final String CACHE_HEALTH_INFO = "healthInfo";
    public static final String CACHE_USER_INFO = "userInfo";
    public static final String CACHE_HEALTH_DATA = "healthData";
    public static final String CACHE_HEALTH_CHART = "healthChart";
    public static final String CACHE_COMMUNITY = "community";
    public static final String CACHE_PATIENTS = "patients";
    public static final String CACHE_MEDICAL_RECORDS = "medical-records";
    
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    public static final Duration HEALTH_SCORE_TTL = Duration.ofHours(1);
    public static final Duration HEALTH_INFO_TTL = Duration.ofMinutes(15);
    public static final Duration USER_INFO_TTL = Duration.ofHours(2);
    public static final Duration HEALTH_DATA_TTL = Duration.ofMinutes(10);
    public static final Duration HEALTH_CHART_TTL = Duration.ofMinutes(5);
    public static final Duration COMMUNITY_TTL = Duration.ofHours(1);
    public static final Duration PATIENTS_TTL = Duration.ofHours(24);
    public static final Duration MEDICAL_RECORDS_TTL = Duration.ofHours(12);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CACHE_HEALTH_SCORE, defaultConfig.entryTtl(HEALTH_SCORE_TTL));
        cacheConfigurations.put(CACHE_HEALTH_INFO, defaultConfig.entryTtl(HEALTH_INFO_TTL));
        cacheConfigurations.put(CACHE_USER_INFO, defaultConfig.entryTtl(USER_INFO_TTL));
        cacheConfigurations.put(CACHE_HEALTH_DATA, defaultConfig.entryTtl(HEALTH_DATA_TTL));
        cacheConfigurations.put(CACHE_HEALTH_CHART, defaultConfig.entryTtl(HEALTH_CHART_TTL));
        cacheConfigurations.put(CACHE_COMMUNITY, defaultConfig.entryTtl(COMMUNITY_TTL));
        cacheConfigurations.put(CACHE_PATIENTS, defaultConfig.entryTtl(PATIENTS_TTL));
        cacheConfigurations.put(CACHE_MEDICAL_RECORDS, defaultConfig.entryTtl(MEDICAL_RECORDS_TTL));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("healthcare-async-");
        executor.initialize();
        return executor;
    }
}

