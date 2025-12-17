package com.sleekydz86.api.gateway.controller;

import com.sleekydz86.api.gateway.client.AuthClient;
import com.sleekydz86.api.gateway.client.HealthcareClient;
import com.sleekydz86.api.gateway.client.TokenRequest;
import com.sleekydz86.api.gateway.dto.Patient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final HealthcareClient healthcareClient;
    private final AuthClient authClient;

    public GatewayController(HealthcareClient healthcareClient, AuthClient authClient) {
        this.healthcareClient = healthcareClient;
        this.authClient = authClient;
    }

    @GetMapping("/healthcare/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(token);
        if (!authClient.validateToken(tokenRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Patient patient = healthcareClient.getPatient(id);
        return ResponseEntity.ok(patient);
    }

    @PostMapping("/healthcare/patients")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient,
                                              @RequestHeader("Authorization") String token) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(token);
        if (!authClient.validateToken(tokenRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Patient createdPatient = healthcareClient.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/fallback/healthcare")
    public ResponseEntity<Map<String, String>> healthcareFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Healthcare service is temporarily unavailable");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

