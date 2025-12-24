package com.sleekydz86.service.healthcare.core.eventsourcing;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventRepository {
    List<EventEntity> findByAggregateIdOrderByVersionAsc(@Param("aggregateId") String aggregateId);
    int getLatestVersion(@Param("aggregateId") String aggregateId);
    List<EventEntity> findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(
        @Param("aggregateId") String aggregateId, 
        @Param("fromVersion") int fromVersion);
    List<String> findAllDistinctAggregateIds();
    void insert(EventEntity eventEntity);
}
