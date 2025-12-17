package com.sleekydz86.service.healthcare.core.query.handler;

import com.sleekydz86.api.gateway.cqrs.query.QueryHandler;
import com.sleekydz86.service.healthcare.core.query.GetPatientQuery;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class GetPatientQueryHandler implements QueryHandler<GetPatientQuery, PatientReadModel> {

    @Autowired
    private PatientReadModelRepository patientReadModelRepository;

    @Override
    public CompletableFuture<PatientReadModel> handle(GetPatientQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            return patientReadModelRepository.findByPatientId(query.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        });
    }

    @Override
    public Class<GetPatientQuery> getQueryType() {
        return GetPatientQuery.class;
    }
}

