package com.sleekydz86.service.llm.application;

import com.sleekydz86.service.llm.domain.model.*;
import com.sleekydz86.service.llm.infrastructure.adapter.auxiliary.KmBertService;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnhancedHealthcareService {

    private final GenerateLLMUseCase generateLLMUseCase;
    private final KmBertService kmBertService;

    public LLMGenerationResult generateWithMedicalContext(LLMGenerationRequest request) {
        Prompt originalPrompt = request.getPrompt();
        String originalContent = originalPrompt.getFinalContent();

        if (!kmBertService.isMedicalRelated(originalContent)) {
            return generateLLMUseCase.generate(request);
        }

        MedicalEntities entities = kmBertService.extractMedicalEntities(originalContent);
        String enhancedContent = buildEnhancedPrompt(originalContent, entities);

        Prompt enhancedPrompt = Prompt.builder()
                .content(enhancedContent)
                .template(originalPrompt.getTemplate())
                .variables(originalPrompt.getVariables())
                .systemPrompt(originalPrompt.getSystemPrompt())
                .build();

        LLMGenerationRequest enhancedRequest = LLMGenerationRequest.builder()
                .prompt(enhancedPrompt)
                .maxTokens(request.getMaxTokens())
                .temperature(request.getTemperature())
                .topP(request.getTopP())
                .topK(request.getTopK())
                .repeatPenalty(request.getRepeatPenalty())
                .stream(request.getStream())
                .requestType("healthcare")
                .build();

        return generateLLMUseCase.generate(enhancedRequest);
    }

    private String buildEnhancedPrompt(String originalContent, MedicalEntities entities) {
        StringBuilder enhanced = new StringBuilder(originalContent);

        if (!entities.getDiseases().isEmpty() || !entities.getSymptoms().isEmpty() || 
            !entities.getMedications().isEmpty() || !entities.getBodyParts().isEmpty()) {
            
            enhanced.append("\n\n[의료 컨텍스트 정보]\n");
            
            if (!entities.getDiseases().isEmpty()) {
                enhanced.append("관련 질환: ").append(String.join(", ", entities.getDiseases())).append("\n");
            }
            
            if (!entities.getSymptoms().isEmpty()) {
                enhanced.append("증상: ").append(String.join(", ", entities.getSymptoms())).append("\n");
            }
            
            if (!entities.getMedications().isEmpty()) {
                enhanced.append("약물: ").append(String.join(", ", entities.getMedications())).append("\n");
            }
            
            if (!entities.getBodyParts().isEmpty()) {
                enhanced.append("관련 부위: ").append(String.join(", ", entities.getBodyParts())).append("\n");
            }
        }

        return enhanced.toString();
    }
}

