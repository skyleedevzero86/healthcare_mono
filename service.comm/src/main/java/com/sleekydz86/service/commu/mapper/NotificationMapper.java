package com.sleekydz86.service.commu.mapper;

import com.sleekydz86.service.commu.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    int insertNotification(Notification notification);
    List<Notification> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
}

