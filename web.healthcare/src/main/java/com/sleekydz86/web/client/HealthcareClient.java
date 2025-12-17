package com.sleekydz86.web.client;

import com.sleekydz86.web.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service.healthcare", fallback = HealthcareClientFallback.class)
public interface HealthcareClient {

    @GetMapping("/api/healthcare/patients/{id}")
    Patient getPatient(@PathVariable("id") Long id);
}

