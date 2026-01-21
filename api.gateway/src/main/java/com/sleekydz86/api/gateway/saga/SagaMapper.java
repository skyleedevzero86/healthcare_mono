package com.sleekydz86.api.gateway.saga;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface SagaMapper {
    
    int countBySagaId(@Param("sagaId") String sagaId);
    
    int insert(SagaEntity saga);
    
    int update(SagaEntity saga);
    
    SagaEntity selectById(@Param("sagaId") String sagaId);
    
    SagaEntity selectBySagaType(@Param("sagaType") String sagaType);
}
