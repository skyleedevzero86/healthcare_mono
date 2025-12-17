package com.sleekydz86.api.gateway.client;

import com.sleekydz86.api.gateway.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service.healthcare", fallback = HealthcareClientFallback.class)
public interface HealthcareClient {

    @GetMapping("/api/healthcare/patients/{id}")
    Patient getPatient(@PathVariable("id") Long id);

    @PostMapping("/api/healthcare/patients")
    Patient createPatient(@RequestBody Patient patient);
}

