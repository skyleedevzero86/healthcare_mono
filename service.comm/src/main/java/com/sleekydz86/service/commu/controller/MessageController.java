package com.sleekydz86.service.commu.controller;

import com.sleekydz86.service.commu.dto.NotificationRequest;
import com.sleekydz86.service.commu.domain.Notification;
import com.sleekydz86.service.commu.service.MessageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communication")
public class MessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageService messageService;

    @PostMapping("/notifications")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        try {
            rabbitTemplate.convertAndSend("healthcare.exchange", "notifications", request);
            messageService.saveNotification(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = messageService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
}

