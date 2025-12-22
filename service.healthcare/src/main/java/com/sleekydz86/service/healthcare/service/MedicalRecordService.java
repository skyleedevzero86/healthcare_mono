package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.service.sharding.MedicalRecordShardingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordShardingService shardingService;

    public MedicalRecordService(MedicalRecordShardingService shardingService) {
        this.shardingService = shardingService;
    }

    public MedicalRecord createMedicalRecord(MedicalRecord record) {
        return shardingService.createMedicalRecord(record);
    }
}

