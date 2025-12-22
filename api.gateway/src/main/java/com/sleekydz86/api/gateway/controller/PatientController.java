package com.sleekydz86.api.gateway.controller;

import com.sleekydz86.api.gateway.cqrs.command.CommandBus;
import com.sleekydz86.api.gateway.cqrs.query.QueryBus;
import com.sleekydz86.api.gateway.dto.CreatePatientRequest;
// TODO: 서비스 간 직접 의존성 제거 필요 - HTTP 통신으로 변경
// import com.sleekydz86.service.healthcare.core.command.CreatePatientCommand;
// import com.sleekydz86.service.healthcare.core.query.GetPatientQuery;
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
        // TODO: 서비스 간 직접 의존성 제거 필요 - HTTP 통신으로 변경
        // CreatePatientCommand command = new CreatePatientCommand(
        //     request.getPatientId(),
        //     request.getPatientName(),
        //     request.getPhoneNumber(),
        //     request.getEmail(),
        //     request.getAddress(),
        //     request.getMedicalHistory()
        // );

        // return commandBus.send(command)
        //     .thenApply(result -> ResponseEntity.ok("환자 생성 완료: " + result))
        //     .exceptionally(throwable -> ResponseEntity.badRequest().body("오류: " + throwable.getMessage()));
        
        return CompletableFuture.completedFuture(
            ResponseEntity.ok("환자 생성 기능은 HTTP 통신으로 구현 필요")
        );
    }

    @GetMapping("/{patientId}")
    public CompletableFuture<ResponseEntity<Object>> getPatient(@PathVariable String patientId) {
        // TODO: 서비스 간 직접 의존성 제거 필요 - HTTP 통신으로 변경
        // GetPatientQuery query = new GetPatientQuery(patientId);

        // return queryBus.send(query)
        //     .thenApply(result -> ResponseEntity.ok(result))
        //     .exceptionally(throwable -> ResponseEntity.badRequest().body("오류: " + throwable.getMessage()));
        
        return CompletableFuture.completedFuture(
            ResponseEntity.ok("환자 조회 기능은 HTTP 통신으로 구현 필요")
        );
    }
}

