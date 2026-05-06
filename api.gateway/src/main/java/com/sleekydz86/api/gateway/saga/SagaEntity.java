package com.sleekydz86.api.gateway.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaEntity {
    private String sagaId;
    private String sagaType;
    private String status;
    private String data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
