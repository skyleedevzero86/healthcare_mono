package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CrossShardQueryService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> searchPatientsAcrossShards(String name) {
        List<CompletableFuture<List<Patient>>> futures = new ArrayList<>();

        CompletableFuture<List<Patient>> future = CompletableFuture.supplyAsync(() ->
            patientRepository.findByNameContaining(name)
        );
        futures.add(future);

        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    public List<Patient> findAllPatients() {
        List<CompletableFuture<List<Patient>>> futures = new ArrayList<>();

        CompletableFuture<List<Patient>> future = CompletableFuture.supplyAsync(() ->
            patientRepository.findAll()
        );
        futures.add(future);

        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    public Patient findPatientByEmail(String email) {
        CompletableFuture<Patient> future = CompletableFuture.supplyAsync(() ->
            patientRepository.findByEmail(email)
        );
        return future.join();
    }
}

