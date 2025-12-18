package com.sleekydz86.service.llm.ports.outbound;

import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;

public interface CacheRepository {
    LLMGenerationResult get(Prompt prompt);

    void save(Prompt prompt, LLMGenerationResult result);

    void evict(Prompt prompt);
}

