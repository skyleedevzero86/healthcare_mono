package com.sleekydz86.service.healthcare.core.eventsourcing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("SELECT e FROM EventEntity e WHERE e.aggregateId = :aggregateId ORDER BY e.version ASC")
    List<EventEntity> findByAggregateIdOrderByVersionAsc(@Param("aggregateId") String aggregateId);

    @Query("SELECT COALESCE(MAX(e.version), 0) FROM EventEntity e WHERE e.aggregateId = :aggregateId")
    int getLatestVersion(@Param("aggregateId") String aggregateId);
}
