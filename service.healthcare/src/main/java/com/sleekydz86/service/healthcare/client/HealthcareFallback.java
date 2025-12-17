package com.sleekydz86.service.healthcare.client;

import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HealthcareFallback implements HealthcareClient {

    @Override
    public Patient getPatient(Long id) {
        Patient patient = new Patient();
        patient.setPatientId(id);
        patient.setName("서비스 사용 불가");
        return patient;
    }

    @Override
    public Patient createPatient(Patient patient) {
        throw new BusinessException("헬스케어 서비스가 일시적으로 사용할 수 없습니다", ApiResultCode.UNKOWN_ERR);
    }

    @Override
    public List<MedicalRecord> getMedicalRecords(Long patientId) {
        return Collections.emptyList();
    }
}
