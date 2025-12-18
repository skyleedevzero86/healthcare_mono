package com.sleekydz86.service.llm.ports.inbound;

import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;

public interface GenerateLLMUseCase {
    LLMGenerationResult generate(LLMGenerationRequest request);

    void generateStream(LLMGenerationRequest request, StreamChunkHandler handler);

    LLMGenerationResult generateWithHistory(LLMGenerationRequest request, String conversationId);

    boolean isAvailable();

    @FunctionalInterface
    interface StreamChunkHandler {
        void onChunk(String chunk);
    }
}

