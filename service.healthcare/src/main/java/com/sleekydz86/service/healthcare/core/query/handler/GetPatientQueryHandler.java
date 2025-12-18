package com.sleekydz86.service.healthcare.core.query.handler;

import com.sleekydz86.api.gateway.cqrs.query.QueryHandler;
import com.sleekydz86.service.healthcare.core.query.GetPatientQuery;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetPatientQueryHandler implements QueryHandler<GetPatientQuery, PatientReadModel> {

    private final PatientReadModelRepository patientReadModelRepository;

    @Override
    public CompletableFuture<PatientReadModel> handle(GetPatientQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<PatientReadModel> readModelOpt = 
                    patientReadModelRepository.findByPatientId(query.getPatientId());
                
                if (readModelOpt.isEmpty()) {
                    throw new BusinessException("환자를 찾을 수 없습니다: " + query.getPatientId(), 
                        ApiResultCode.RESULT_IS_EMPTY);
                }
                
                log.debug("환자 쿼리 실행: {}", query.getPatientId());
                return readModelOpt.get();
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("환자 조회 실패", e);
                throw new BusinessException("환자 조회 실패", e, ApiResultCode.UNKOWN_ERR);
            }
        });
    }

    @Override
    public Class<GetPatientQuery> getQueryType() {
        return GetPatientQuery.class;
    }
}
