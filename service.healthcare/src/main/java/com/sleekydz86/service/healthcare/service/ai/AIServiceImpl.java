package com.sleekydz86.service.healthcare.service.ai;

import com.sleekydz86.service.healthcare.service.ChatgptService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final ChatgptService chatgptService;

    @Override
    public String generateResponse(Map<String, Object> params) {
        String query = (String) params.get("query");
        if (query != null && !query.isEmpty()) {
            return chatgptService.sendMessage(query);
        }
        return "";
    }
}

