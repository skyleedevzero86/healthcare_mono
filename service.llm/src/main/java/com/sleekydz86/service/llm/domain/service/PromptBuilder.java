package com.sleekydz86.service.llm.domain.service;

import com.sleekydz86.service.llm.domain.model.Prompt;

import java.util.Map;

public interface PromptBuilder {
    Prompt buildHealthcarePrompt(Map<String, Object> bioInfo);

    Prompt buildGeneralPrompt(String userPrompt, Map<String, Object> variables);

    String getSystemPrompt(String template);
}

