package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.service.sharding.MedicalRecordShardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalRecordService {

    @Autowired
    private MedicalRecordShardingService shardingService;

    public MedicalRecord createMedicalRecord(MedicalRecord record) {
        return shardingService.createMedicalRecord(record);
    }
}

