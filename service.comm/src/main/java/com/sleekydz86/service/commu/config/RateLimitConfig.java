package com.sleekydz86.service.commu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private static final int MAX_REQUESTS = 50;
    private static final long WINDOW_MS = 60000;
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Bean
    public HandlerInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(rateLimitMap, MAX_REQUESTS, WINDOW_MS);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
            .addPathPatterns("/community/v1/**");
    }

    static class RateLimitInfo {
        AtomicInteger count = new AtomicInteger(0);
        long resetTime = System.currentTimeMillis() + WINDOW_MS;
    }

    static class RateLimitInterceptor implements HandlerInterceptor {
        private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap;
        private final int maxRequests;
        private final long windowMs;

        public RateLimitInterceptor(ConcurrentHashMap<String, RateLimitInfo> rateLimitMap, 
                                   int maxRequests, long windowMs) {
            this.rateLimitMap = rateLimitMap;
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        @Override
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, 
                               jakarta.servlet.http.HttpServletResponse response, 
                               Object handler) throws Exception {
            String clientId = getClientId(request);
            RateLimitInfo info = rateLimitMap.computeIfAbsent(clientId, k -> new RateLimitInfo());
            
            long now = System.currentTimeMillis();
            if (now > info.resetTime) {
                info.count.set(0);
                info.resetTime = now + windowMs;
            }
            
            int currentCount = info.count.incrementAndGet();
            if (currentCount > maxRequests) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"resultCode\":\"4291\",\"resultMessage\":\"요청 한도를 초과했습니다.\",\"resultData\":null}");
                return false;
            }
            
            return true;
        }

        private String getClientId(jakarta.servlet.http.HttpServletRequest request) {
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                return userId;
            }
            return request.getRemoteAddr();
        }
    }
}

