package com.sleekydz86.service.healthcare.core.readmodel;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientReadModelRepositoryImpl implements PatientReadModelRepository {

    private final PatientRepository patientRepository;

    @Override
    public Optional<PatientReadModel> findByPatientId(String patientId) {
        try {
            Long id = Long.parseLong(patientId);
            return patientRepository.findById(id)
                .map(this::toReadModel);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(PatientReadModel patientReadModel) {
        Patient patient = toEntity(patientReadModel);
        patientRepository.save(patient);
    }

    @Override
    public void deleteByPatientId(String patientId) {
        try {
            Long id = Long.parseLong(patientId);
            patientRepository.deleteById(id);
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
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page, size);
        return patientRepository.findAll(pageable)
            .stream()
            .map(this::toReadModel)
            .collect(java.util.stream.Collectors.toList());
    }
}

