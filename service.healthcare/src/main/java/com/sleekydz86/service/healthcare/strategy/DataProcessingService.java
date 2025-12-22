package com.sleekydz86.service.healthcare.strategy;

import java.util.List;
import java.util.Map;

public class DataProcessingService {
    private final List<DataProcessor> processors;

    public DataProcessingService(List<DataProcessor> processors) {
        this.processors = processors;
    }

    public Map<String, Object> processData(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            for (DataProcessor processor : processors) {
                if (processor.supports(value)) {
                    Map<String, Object> singleEntryMap = Map.of(entry.getKey(), value);
                    Map<String, Object> processed = processor.process(singleEntryMap);
                    data.put(entry.getKey(), processed.get(entry.getKey()));
                    break;
                }
            }
        }
        return data;
    }
}

