package com.sleekydz86.service.llm.ports.outbound;

import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;

import java.util.function.Consumer;

public interface LLMProvider {
    LLMGenerationResult generate(LLMGenerationRequest request);

    void generateStream(LLMGenerationRequest request, Consumer<String> onChunk);

    boolean isAvailable();
}

