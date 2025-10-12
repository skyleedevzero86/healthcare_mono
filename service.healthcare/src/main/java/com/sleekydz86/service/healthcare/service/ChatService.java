package com.sleekydz86.service.healthcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatgptService chatgptService;
    private final ChatgptProperties cp;

    public String getChatResponse(String gptQuery) {
        cp.setModel("gpt-3.5-turbo-instruct");
        cp.setTemperature(0.4);
        return chatgptService.sendMessage(gptQuery);
    }
}
