package com.sleekydz86.api.gateway.controller;

import com.sleekydz86.api.gateway.dto.ApiResponse;
import com.sleekydz86.api.gateway.dto.ApiResultCode;
import com.sleekydz86.api.gateway.dto.PatientRegistrationRequest;
import com.sleekydz86.api.gateway.saga.SagaOrchestrator;
import com.sleekydz86.api.gateway.saga.SagaResult;
import com.sleekydz86.api.gateway.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/patient-registration")
@RequiredArgsConstructor
public class PatientRegistrationController {

    private final SagaOrchestrator sagaOrchestrator;

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<SagaResult>>> registerPatient(
            @RequestBody PatientRegistrationRequest request) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("환자 등록 요청 수신: patientName={}, email={}", 
                    request.getPatientName(), request.getEmail());
                
                // PatientRegistrationData 생성
                com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga.PatientRegistrationData sagaData =
                    new com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga.PatientRegistrationData(
                        null, // patientId는 CreatePatientStep에서 생성됨
                        request.getPatientName(),
                        request.getPhoneNumber(),
                        request.getEmail(),
                        request.getAddress(),
                        request.getMedicalHistory()
                    );
                
                // Saga 생성
                com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga saga =
                    new com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga(sagaData);
                
                // Saga 실행
                CompletableFuture<SagaResult> sagaFuture = sagaOrchestrator.executeSaga(saga);
                SagaResult result = sagaFuture.join();
                
                if (result.isSuccess()) {
                    log.info("환자 등록 성공: sagaId={}", saga.getSagaId());
                    ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                        .resultCode(ApiResultCode.SUCCESS.getCode())
                        .resultMessage("환자 등록이 완료되었습니다")
                        .resultData(result)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.ok(response);
                } else {
                    log.warn("환자 등록 실패: sagaId={}, message={}", 
                        saga.getSagaId(), result.getMessage());
                    ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                        .resultCode(ApiResultCode.UNKNOWN_ERR.getCode())
                        .resultMessage(result.getMessage())
                        .resultData(result)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            } catch (Exception e) {
                log.error("환자 등록 실패", e);
                ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                    .resultCode(ApiResultCode.UNKNOWN_ERR.getCode())
                    .resultMessage("환자 등록 실패: " + e.getMessage())
                    .resultData(null)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        });
    }
    
    @GetMapping("/{sagaId}/status")
    public CompletableFuture<ResponseEntity<ApiResponse<SagaStatus>>> getSagaStatus(
            @PathVariable String sagaId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                UUID uuid = UUID.fromString(sagaId);
                CompletableFuture<SagaStatus> statusFuture = sagaOrchestrator.getSagaStatus(uuid);
                SagaStatus status = statusFuture.join();
                
                if (status != null) {
                    ApiResponse<SagaStatus> response = ApiResponse.<SagaStatus>builder()
                        .resultCode(ApiResultCode.SUCCESS.getCode())
                        .resultMessage("Saga 상태 조회 성공")
                        .resultData(status)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.ok(response);
                } else {
                    ApiResponse<SagaStatus> response = ApiResponse.<SagaStatus>builder()
                        .resultCode(ApiResultCode.RESULT_IS_EMPTY.getCode())
                        .resultMessage("Saga를 찾을 수 없습니다")
                        .resultData(null)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } catch (Exception e) {
                log.error("Saga 상태 조회 실패", e);
                ApiResponse<SagaStatus> response = ApiResponse.<SagaStatus>builder()
                    .resultCode(ApiResultCode.UNKNOWN_ERR.getCode())
                    .resultMessage("Saga 상태 조회 실패: " + e.getMessage())
                    .resultData(null)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        });
    }
    
    @PostMapping("/{sagaId}/resume")
    public CompletableFuture<ResponseEntity<ApiResponse<SagaResult>>> resumeSaga(
            @PathVariable String sagaId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                UUID uuid = UUID.fromString(sagaId);
                CompletableFuture<SagaResult> sagaFuture = sagaOrchestrator.resumeSaga(uuid);
                SagaResult result = sagaFuture.join();
                
                if (result.isSuccess()) {
                    ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                        .resultCode(ApiResultCode.SUCCESS.getCode())
                        .resultMessage("Saga 재개 성공")
                        .resultData(result)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.ok(response);
                } else {
                    ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                        .resultCode(ApiResultCode.UNKNOWN_ERR.getCode())
                        .resultMessage(result.getMessage())
                        .resultData(null)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            } catch (Exception e) {
                log.error("Saga 재개 실패", e);
                ApiResponse<SagaResult> response = ApiResponse.<SagaResult>builder()
                    .resultCode(ApiResultCode.UNKNOWN_ERR.getCode())
                    .resultMessage("Saga 재개 실패: " + e.getMessage())
                    .resultData(null)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        });
    }
}

