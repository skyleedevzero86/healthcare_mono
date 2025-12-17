package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.timestamp DESC")
    List<Notification> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
}

