package com.sleekydz86.service.healthcare.service.ai;

import com.sleekydz86.service.healthcare.service.ChatService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final ChatService chatService;

    @Override
    public String generateResponse(Map<String, Object> params) {
        return chatService.getChatResponse(params);
    }
}
