package com.sleekydz86.api.gateway.controller;

import com.sleekydz86.api.gateway.cqrs.command.CommandBus;
import com.sleekydz86.api.gateway.cqrs.query.QueryBus;
import com.sleekydz86.api.gateway.dto.CreatePatientRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public PatientController(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createPatient(@RequestBody CreatePatientRequest request) {
        return CompletableFuture.completedFuture(
            ResponseEntity.ok("환자 생성 기능은 HTTP 통신으로 구현 필요")
        );
    }

    @GetMapping("/{patientId}")
    public CompletableFuture<ResponseEntity<Object>> getPatient(@PathVariable String patientId) {
        return CompletableFuture.completedFuture(
            ResponseEntity.ok("환자 조회 기능은 HTTP 통신으로 구현 필요")
        );
    }
}

