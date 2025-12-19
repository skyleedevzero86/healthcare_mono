package com.sleekydz86.service.healthcare.core.readmodel;

import java.util.List;
import java.util.Optional;

public interface PatientReadModelRepository {
    Optional<PatientReadModel> findByPatientId(String patientId);
    void save(PatientReadModel patientReadModel);
    void deleteByPatientId(String patientId);
    List<PatientReadModel> findAll(int page, int size);
}

