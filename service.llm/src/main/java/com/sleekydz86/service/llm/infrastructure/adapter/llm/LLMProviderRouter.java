package com.sleekydz86.service.llm.infrastructure.adapter.llm;

import com.sleekydz86.service.llm.config.RoutingProperties;
import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.ports.outbound.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component("llmProviderRouter")
@RequiredArgsConstructor
public class LLMProviderRouter implements LLMProvider {

    private final Map<String, LLMProvider> providers;
    private final RoutingProperties routingProperties;

    public LLMProviderRouter(
            LlamaCppProvider llamaCppProvider,
            Glm47Provider glm47Provider,
            RoutingProperties routingProperties) {
        this.routingProperties = routingProperties;
        this.providers = new HashMap<>();
        this.providers.put("llama-cpp", llamaCppProvider);
        this.providers.put("glm47", glm47Provider);
    }

    @Override
    public LLMGenerationResult generate(LLMGenerationRequest request) {
        LLMProvider provider = selectProvider(request);
        log.debug("Provider 선택: {}", getProviderName(provider));
        return provider.generate(request);
    }

    @Override
    public void generateStream(LLMGenerationRequest request, Consumer<String> onChunk) {
        LLMProvider provider = selectProvider(request);
        log.debug("Provider 선택 (Stream): {}", getProviderName(provider));
        provider.generateStream(request, onChunk);
    }

    @Override
    public boolean isAvailable() {
        return providers.values().stream()
                .anyMatch(LLMProvider::isAvailable);
    }

    private LLMProvider selectProvider(LLMGenerationRequest request) {
        String requestType = request.getRequestType();
        String providerName = null;

        if ("chat".equals(requestType)) {
            providerName = routingProperties.getChat();
        } else if ("healthcare".equals(requestType)) {
            providerName = routingProperties.getHealthcare();
        } else {
            providerName = routingProperties.getDefaultProvider();
        }

        LLMProvider provider = providers.get(providerName);
        if (provider == null) {
            log.warn("Provider '{}'를 찾을 수 없습니다. 기본 Provider 사용: {}", 
                    providerName, routingProperties.getDefaultProvider());
            provider = providers.get(routingProperties.getDefaultProvider());
        }

        if (provider == null) {
            throw new IllegalStateException("사용 가능한 LLM Provider가 없습니다.");
        }

        return provider;
    }

    private String getProviderName(LLMProvider provider) {
        return providers.entrySet().stream()
                .filter(entry -> entry.getValue() == provider)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("unknown");
    }
}

