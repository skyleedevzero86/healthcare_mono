package com.sleekydz86.service.healthcare.core.query.handler;

import com.sleekydz86.api.gateway.cqrs.query.QueryHandler;
import com.sleekydz86.service.healthcare.core.query.GetPatientQuery;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class GetPatientQueryHandler implements QueryHandler<GetPatientQuery, PatientReadModel> {

    private final PatientReadModelRepository patientReadModelRepository;

    @Override
    public CompletableFuture<PatientReadModel> handle(GetPatientQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            return patientReadModelRepository.findByPatientId(query.getPatientId())
                .orElseThrow(() -> new BusinessException("환자를 찾을 수 없습니다", ApiResultCode.RESULT_IS_EMPTY));
        });
    }

    @Override
    public Class<GetPatientQuery> getQueryType() {
        return GetPatientQuery.class;
    }
}

