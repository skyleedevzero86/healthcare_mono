package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.entity.Notification;
import com.sleekydz86.service.commu.dto.NotificationRequest;
import com.sleekydz86.service.commu.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
            .userId(request.getUserId())
            .message(request.getMessage())
            .type(request.getType())
            .timestamp(Instant.now())
            .build();

        notificationRepository.save(notification);

        String cacheKey = "notifications:" + request.getUserId();
        redisTemplate.opsForList().leftPush(cacheKey, notification);
        redisTemplate.expire(cacheKey, Duration.ofHours(24));
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        String cacheKey = "notifications:" + userId;

        List<Object> cached = redisTemplate.opsForList().range(cacheKey, 0, -1);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream()
                .map(obj -> (Notification) obj)
                .collect(Collectors.toList());
        }

        List<Notification> notifications = notificationRepository.findByUserIdOrderByTimestampDesc(userId);

        if (!notifications.isEmpty()) {
            notifications.forEach(notification ->
                redisTemplate.opsForList().leftPush(cacheKey, notification));
            redisTemplate.expire(cacheKey, Duration.ofHours(24));
        }

        return notifications;
    }
}

