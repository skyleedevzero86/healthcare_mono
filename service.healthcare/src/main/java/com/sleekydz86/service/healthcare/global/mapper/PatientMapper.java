package com.sleekydz86.service.healthcare.global.mapper;

import com.sleekydz86.service.healthcare.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientMapper {
    Patient findByPatientIdAndShardKey(Long patientId, Integer shardKey);
    List<Patient> findByRegionIdAndShardKey(String regionId, Integer shardKey);
    Patient findById(Long patientId);
    int insert(Patient patient);
    int update(Patient patient);
    int deleteById(Long patientId);
}

