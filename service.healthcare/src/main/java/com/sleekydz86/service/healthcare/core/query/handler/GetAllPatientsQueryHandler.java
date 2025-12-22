package com.sleekydz86.service.healthcare.core.query.handler;

import com.sleekydz86.api.gateway.cqrs.query.QueryHandler;
import com.sleekydz86.service.healthcare.core.query.GetAllPatientsQuery;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllPatientsQueryHandler implements QueryHandler<GetAllPatientsQuery, List<PatientReadModel>> {

    private final PatientReadModelRepository patientReadModelRepository;

    @Override
    public CompletableFuture<List<PatientReadModel>> handle(GetAllPatientsQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<PatientReadModel> patients = patientReadModelRepository.findAll(
                    query.getPage(), query.getSize());
                
                log.debug("GetAllPatients 쿼리 실행 완료. 조회된 환자 수: {}", patients.size());
                return patients;
            } catch (Exception e) {
                log.error("전체 환자 조회 실패", e);
                throw new RuntimeException("환자 목록 조회 실패", e);
            }
        });
    }

    @Override
    public Class<GetAllPatientsQuery> getQueryType() {
        return GetAllPatientsQuery.class;
    }
}

