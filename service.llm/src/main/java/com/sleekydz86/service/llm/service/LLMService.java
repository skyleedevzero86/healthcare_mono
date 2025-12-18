package com.sleekydz86.service.llm.service;

import com.sleekydz86.service.llm.dto.LLMRequest;
import com.sleekydz86.service.llm.dto.LLMResponse;

public interface LLMService {
    LLMResponse generate(LLMRequest request);
    void generateStream(LLMRequest request, java.util.function.Consumer<String> onChunk);
    LLMResponse generateWithHistory(LLMRequest request);
    boolean isAvailable();
}

