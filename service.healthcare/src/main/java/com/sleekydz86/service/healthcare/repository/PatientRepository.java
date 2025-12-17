package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Patient findByPatientIdAndShardKey(Long patientId, Integer shardKey);

    List<Patient> findByRegionIdAndShardKey(String regionId, Integer shardKey);

    List<Patient> findByRegionId(String regionId);

    @Query("SELECT p FROM Patient p WHERE p.name LIKE %:name%")
    List<Patient> findByNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Patient p WHERE p.email = :email")
    Patient findByEmail(@Param("email") String email);
}

