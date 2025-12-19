package com.sleekydz86.service.llm.application.mapper;

import com.sleekydz86.service.llm.domain.model.*;
import com.sleekydz86.service.llm.dto.BioInfoDto;
import com.sleekydz86.service.llm.dto.HealthcarePromptRequest;
import com.sleekydz86.service.llm.dto.LLMRequest;
import com.sleekydz86.service.llm.dto.LLMResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LLMMapper {

    public static LLMGenerationRequest toDomain(LLMRequest dto, Prompt prompt) {
        return LLMGenerationRequest.builder()
                .prompt(prompt)
                .maxTokens(dto.getMaxTokens())
                .temperature(dto.getTemperature())
                .topP(dto.getTopP())
                .topK(dto.getTopK())
                .repeatPenalty(1.1)
                .stream(dto.getStream() != null && dto.getStream())
                .build();
    }

    public static Prompt toPrompt(LLMRequest dto, String finalContent, String systemPrompt) {
        return Prompt.builder()
                .content(finalContent)
                .template(dto.getTemplate())
                .variables(dto.getVariables())
                .systemPrompt(systemPrompt)
                .build();
    }

    public static LLMResponse toDto(LLMGenerationResult result, String conversationId) {
        return LLMResponse.builder()
                .response(result.getContent())
                .conversationId(conversationId)
                .tokensUsed(result.getTokensUsed())
                .promptTokens(result.getPromptTokens())
                .completionTokens(result.getCompletionTokens())
                .processingTimeMs(result.getProcessingTimeMs())
                .timestamp(result.getTimestamp())
                .metadata(result.getMetadata())
                .build();
    }

    public static com.sleekydz86.service.llm.dto.Message toMessageDto(ConversationMessage message) {
        return com.sleekydz86.service.llm.dto.Message.builder()
                .role(message.getRole())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }

    public static ConversationMessage toDomainMessage(String role, String content) {
        return ConversationMessage.builder()
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static List<com.sleekydz86.service.llm.dto.Message> toMessageDtoList(List<ConversationMessage> messages) {
        return messages.stream()
                .map(LLMMapper::toMessageDto)
                .collect(Collectors.toList());
    }
}

