package com.sleekydz86.web.client;

import com.sleekydz86.web.dto.Patient;
import org.springframework.stereotype.Component;

@Component
public class HealthcareClientFallback implements HealthcareClient {

    @Override
    public Patient getPatient(Long id) {
        return null;
    }
}

