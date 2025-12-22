package com.sleekydz86.service.healthcare.core.readmodel;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.global.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientReadModelRepositoryImpl implements PatientReadModelRepository {

    private final PatientMapper patientMapper;

    @Override
    public Optional<PatientReadModel> findByPatientId(String patientId) {
        try {
            Long id = Long.parseLong(patientId);
            Patient patient = patientMapper.findById(id);
            if (patient != null) {
                return Optional.of(toReadModel(patient));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(PatientReadModel patientReadModel) {
        Patient patient = toEntity(patientReadModel);
        if (patient.getPatientId() != null) {
            patientMapper.update(patient);
        } else {
            patientMapper.insert(patient);
        }
    }

    @Override
    public void deleteByPatientId(String patientId) {
        try {
            Long id = Long.parseLong(patientId);
            patientMapper.deleteById(id);
        } catch (NumberFormatException e) {
        }
    }

    private PatientReadModel toReadModel(Patient patient) {
        return new PatientReadModel(
            String.valueOf(patient.getPatientId()),
            patient.getName(),
            patient.getPhone(),
            patient.getEmail(),
            patient.getAddress(),
            null,
            patient.getCreatedAt(),
            patient.getUpdatedAt()
        );
    }

    private Patient toEntity(PatientReadModel readModel) {
        Patient patient = new Patient();
        if (readModel.getPatientId() != null) {
            try {
                patient.setPatientId(Long.parseLong(readModel.getPatientId()));
            } catch (NumberFormatException e) {
            }
        }
        patient.setName(readModel.getPatientName());
        patient.setPhone(readModel.getPhoneNumber());
        patient.setEmail(readModel.getEmail());
        patient.setAddress(readModel.getAddress());
        patient.setCreatedAt(readModel.getCreatedAt());
        patient.setUpdatedAt(readModel.getUpdatedAt());
        return patient;
    }
    
    @Override
    public List<PatientReadModel> findAll(int page, int size) {
        return new ArrayList<>();
    }
}

