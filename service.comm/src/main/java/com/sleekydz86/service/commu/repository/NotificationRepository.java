package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Notification;
import com.sleekydz86.service.commu.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final NotificationMapper notificationMapper;

    public Notification save(Notification notification) {
        notificationMapper.insertNotification(notification);
        return notification;
    }

    public List<Notification> findByUserIdOrderByTimestampDesc(Long userId) {
        return notificationMapper.findByUserIdOrderByTimestampDesc(userId);
    }
}

