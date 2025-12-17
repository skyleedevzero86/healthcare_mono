package com.sleekydz86.api.gateway.controller;

import com.sleekydz86.api.gateway.cqrs.command.CommandBus;
import com.sleekydz86.api.gateway.cqrs.query.QueryBus;
import com.sleekydz86.service.healthcare.core.command.CreatePatientCommand;
import com.sleekydz86.service.healthcare.core.query.GetPatientQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private QueryBus queryBus;

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createPatient(@RequestBody CreatePatientRequest request) {
        CreatePatientCommand command = new CreatePatientCommand(
            request.getPatientId(),
            request.getPatientName(),
            request.getPhoneNumber(),
            request.getEmail(),
            request.getAddress(),
            request.getMedicalHistory()
        );

        return commandBus.send(command)
            .thenApply(result -> ResponseEntity.ok("Patient created: " + result))
            .exceptionally(throwable -> ResponseEntity.badRequest().body("Error: " + throwable.getMessage()));
    }

    @GetMapping("/{patientId}")
    public CompletableFuture<ResponseEntity<Object>> getPatient(@PathVariable String patientId) {
        GetPatientQuery query = new GetPatientQuery(patientId);

        return queryBus.send(query)
            .thenApply(result -> ResponseEntity.ok(result))
            .exceptionally(throwable -> ResponseEntity.badRequest().body("Error: " + throwable.getMessage()));
    }

    public static class CreatePatientRequest {
        private String patientId;
        private String patientName;
        private String phoneNumber;
        private String email;
        private String address;
        private String medicalHistory;

        public CreatePatientRequest() {}

        public CreatePatientRequest(String patientId, String patientName, String phoneNumber,
                                   String email, String address, String medicalHistory) {
            this.patientId = patientId;
            this.patientName = patientName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.medicalHistory = medicalHistory;
        }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getMedicalHistory() { return medicalHistory; }
        public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    }
}

