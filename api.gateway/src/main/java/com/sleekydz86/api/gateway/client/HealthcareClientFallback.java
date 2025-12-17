package com.sleekydz86.api.gateway.client;

import com.sleekydz86.api.gateway.dto.Patient;
import org.springframework.stereotype.Component;

@Component
public class HealthcareClientFallback implements HealthcareClient {

    @Override
    public Patient getPatient(Long id) {
        return null;
    }

    @Override
    public Patient createPatient(Patient patient) {
        return null;
    }
}

