package com.sleekydz86.api.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DtoConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T convertToEntity(Object dto, Class<T> entityClass) {
        if (dto == null) {
            log.warn("DTO가 null입니다. entityClass={}", entityClass.getSimpleName());
            return null;
        }
        
        try {
            return objectMapper.convertValue(dto, entityClass);
        } catch (IllegalArgumentException e) {
            log.error("DTO 변환 실패: dto={}, entityClass={}", 
                    dto.getClass().getSimpleName(), entityClass.getSimpleName(), e);
            throw new IllegalArgumentException(
                "DTO 변환 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    public <T> T convertToDto(Object entity, Class<T> dtoClass) {
        if (entity == null) {
            log.warn("엔티티가 null입니다. dtoClass={}", dtoClass.getSimpleName());
            return null;
        }
        
        try {
            return objectMapper.convertValue(entity, dtoClass);
        } catch (IllegalArgumentException e) {
            log.error("엔티티 변환 실패: entity={}, dtoClass={}", 
                    entity.getClass().getSimpleName(), dtoClass.getSimpleName(), e);
            throw new IllegalArgumentException(
                "엔티티 변환 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> convertToMap(Object obj) {
        if (obj == null) {
            return new java.util.HashMap<>();
        }
        
        try {
            return objectMapper.convertValue(obj, java.util.Map.class);
        } catch (Exception e) {
            log.error("Map 변환 실패: obj={}", obj.getClass().getSimpleName(), e);
            throw new IllegalArgumentException(
                "Map 변환 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    public <T> T convertFromMap(java.util.Map<String, Object> map, Class<T> targetClass) {
        if (map == null || map.isEmpty()) {
            log.warn("Map이 null이거나 비어있습니다. targetClass={}", targetClass.getSimpleName());
            return null;
        }
        
        try {
            return objectMapper.convertValue(map, targetClass);
        } catch (IllegalArgumentException e) {
            log.error("Map에서 객체 변환 실패: targetClass={}", targetClass.getSimpleName(), e);
            throw new IllegalArgumentException(
                "Map에서 객체 변환 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 변환 실패: obj={}", obj.getClass().getSimpleName(), e);
            throw new IllegalArgumentException(
                "JSON 변환 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    public <T> T fromJson(String json, Class<T> targetClass) {
        if (json == null || json.trim().isEmpty()) {
            log.warn("JSON 문자열이 null이거나 비어있습니다. targetClass={}", targetClass.getSimpleName());
            return null;
        }
        
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            log.error("JSON 파싱 실패: targetClass={}", targetClass.getSimpleName(), e);
            throw new IllegalArgumentException(
                "JSON 파싱 중 오류가 발생했습니다: " + e.getMessage(), 
                e
            );
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}


