package com.sleekydz86.service.llm.domain.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentMemory {
    private String agentId;
    private List<MemoryEntry> shortTermMemory;
    private Map<String, Object> longTermMemory;
    private int maxShortTermSize;
    private LocalDateTime lastUpdated;
    
    public static AgentMemory create(String agentId, int maxShortTermSize) {
        return AgentMemory.builder()
                .agentId(agentId)
                .shortTermMemory(new ArrayList<>())
                .longTermMemory(new HashMap<>())
                .maxShortTermSize(maxShortTermSize)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    public void addShortTerm(String content, MemoryType type) {
        if (shortTermMemory == null) {
            shortTermMemory = new ArrayList<>();
        }
        
        if (shortTermMemory.size() >= maxShortTermSize) {
            shortTermMemory.remove(0);
        }
        
        shortTermMemory.add(MemoryEntry.builder()
                .content(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build());
        
        lastUpdated = LocalDateTime.now();
    }
    
    public void addLongTerm(String key, Object value) {
        if (longTermMemory == null) {
            longTermMemory = new HashMap<>();
        }
        longTermMemory.put(key, value);
        lastUpdated = LocalDateTime.now();
    }
    
    public Object getLongTerm(String key) {
        return longTermMemory != null ? longTermMemory.get(key) : null;
    }
    
    public String getRecentContext(int count) {
        if (shortTermMemory == null || shortTermMemory.isEmpty()) {
            return "";
        }
        
        int startIndex = Math.max(0, shortTermMemory.size() - count);
        StringBuilder context = new StringBuilder();
        
        for (int i = startIndex; i < shortTermMemory.size(); i++) {
            MemoryEntry entry = shortTermMemory.get(i);
            context.append(entry.getType().name()).append(": ").append(entry.getContent()).append("\n");
        }
        
        return context.toString();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryEntry {
        private String content;
        private MemoryType type;
        private LocalDateTime timestamp;
    }
    
    public enum MemoryType {
        OBSERVATION,
        ACTION,
        RESULT,
        DECISION,
        ERROR
    }
}

