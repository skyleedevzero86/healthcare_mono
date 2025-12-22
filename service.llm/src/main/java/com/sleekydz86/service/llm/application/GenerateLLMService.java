package com.sleekydz86.service.llm.application;

import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import com.sleekydz86.service.llm.ports.outbound.CacheRepository;
import com.sleekydz86.service.llm.ports.outbound.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateLLMService implements GenerateLLMUseCase {

    private final LLMProvider llmProvider;
    private final CacheRepository cacheRepository;

    @Override
    public LLMGenerationResult generate(LLMGenerationRequest request) {
        long startTime = System.currentTimeMillis();

        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 LLM 생성 요청입니다.");
        }

        try {
            Prompt prompt = request.getPrompt();

            LLMGenerationResult cachedResult = cacheRepository.get(prompt);
            if (cachedResult != null) {
                log.debug("캐시된 응답 반환");
                return LLMGenerationResult.builder()
                        .content(cachedResult.getContent())
                        .tokensUsed(cachedResult.getTokensUsed())
                        .promptTokens(cachedResult.getPromptTokens())
                        .completionTokens(cachedResult.getCompletionTokens())
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .timestamp(LocalDateTime.now())
                        .metadata(cachedResult.getMetadata())
                        .build();
            }

            LLMGenerationResult result = llmProvider.generate(request);
            
            LLMGenerationResult finalResult = LLMGenerationResult.builder()
                    .content(result.getContent())
                    .tokensUsed(result.getTokensUsed())
                    .promptTokens(result.getPromptTokens())
                    .completionTokens(result.getCompletionTokens())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .timestamp(LocalDateTime.now())
                    .metadata(result.getMetadata())
                    .build();

            cacheRepository.save(prompt, finalResult);

            log.info("LLM 응답 생성 완료: tokens={}, time={}ms",
                    finalResult.getTokensUsed(), finalResult.getProcessingTimeMs());

            return finalResult;

        } catch (Exception e) {
            log.error("LLM 응답 생성 중 오류", e);
            throw new RuntimeException("LLM 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateStream(LLMGenerationRequest request, StreamChunkHandler handler) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 LLM 생성 요청입니다.");
        }

        try {
            llmProvider.generateStream(request, handler::onChunk);
        } catch (Exception e) {
            log.error("스트리밍 응답 생성 중 오류", e);
            throw new RuntimeException("스트리밍 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public LLMGenerationResult generateWithHistory(LLMGenerationRequest request, String conversationId) {
        return generate(request);
    }

    @Override
    public boolean isAvailable() {
        return llmProvider.isAvailable();
    }
}

