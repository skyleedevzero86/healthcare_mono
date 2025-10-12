package com.sleekydz86.service.healthcare.service;

import org.springframework.stereotype.Service;

@Service
public class ChatgptService {

    public String sendMessage(String query) {
        return "AI response for: " + query;
    }
}
