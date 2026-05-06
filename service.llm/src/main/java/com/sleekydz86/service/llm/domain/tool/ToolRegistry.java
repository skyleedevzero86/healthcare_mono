package com.sleekydz86.service.llm.domain.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ToolRegistry {
    
    private final Map<String, Tool> tools;
    
    public ToolRegistry(List<Tool> tools) {
        this.tools = new HashMap<>();
        if (tools != null) {
            for (Tool tool : tools) {
                this.tools.put(tool.getName(), tool);
                log.debug("Tool 등록: {}", tool.getName());
            }
        }
    }
    
    public Tool getTool(String toolName) {
        return tools.get(toolName);
    }
    
    public List<Tool> getAllTools() {
        return new ArrayList<>(tools.values());
    }
    
    public List<Tool> getToolsForAction(String action) {
        return tools.values().stream()
                .filter(tool -> tool.canHandle(action))
                .collect(Collectors.toList());
    }
    
    public boolean hasTool(String toolName) {
        return tools.containsKey(toolName);
    }
    
    public List<Map<String, Object>> getToolSchemas() {
        return tools.values().stream()
                .map(tool -> {
                    Map<String, Object> schema = new HashMap<>();
                    schema.put("name", tool.getName());
                    schema.put("description", tool.getDescription());
                    schema.put("schema", tool.getSchema());
                    return schema;
                })
                .collect(Collectors.toList());
    }
}

