package com.sleekydz86.service.healthcare.client;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "service.healthcare", fallback = HealthcareFallback.class)
public interface HealthcareClient {

    @GetMapping("/api/patients/{id}")
    Patient getPatient(@PathVariable("id") Long id);

    @PostMapping("/api/patients")
    Patient createPatient(@RequestBody Patient patient);

    @GetMapping("/api/medical-records/{patientId}")
    List<MedicalRecord> getMedicalRecords(@PathVariable("patientId") Long patientId);
}

