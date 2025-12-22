package com.sleekydz86.service.commu.saga.steps;

import com.sleekydz86.service.commu.dto.NotificationRequest;
import com.sleekydz86.service.commu.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendWelcomeNotificationStep {

    private final MessageService messageService;
}
