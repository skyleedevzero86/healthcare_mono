package com.sleekydz86.service.usermanagement.service.relationship;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.repository.UserRelationshipRepository;
import com.sleekydz86.service.usermanagement.util.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRelationshipServiceImpl implements UserRelationshipService {
    private final UserRelationshipRepository relationshipRepository;
    private final RelationshipValidator relationshipValidator;
    private final DtoConverter dtoConverter;

    @Override
    public ServiceResponse<Integer> updateGuardianMapping(UserDto dto) {
        try {
            relationshipValidator.validate(dto);
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            String str = dto.getGuardian();
            try {
                int cnt = relationshipRepository.countSearchParent(dto);
                ArrayList<Integer> arr = new ArrayList<Integer>();
                if (str != null) {
                    result = dtoConverter.fromJson(str, ArrayList.class);
                    for (Map<String, Object> dt : result) {
                        arr.add((int) dt.get("guardianSeq"));
                        dto.setGuardianSeq((int) dt.get("guardianSeq"));
                        dto.setGuardianId((String) dt.get("guardianId"));
                        relationshipRepository.updateGuardianMapping(dto);
                    }
                }
                if (arr.size() > 0) {
                    dto.setGuardianSeqArray(arr);
                }
                int delCnt = relationshipRepository.deleteParentMapping(dto);
                return ServiceResponse.success(delCnt);
            } catch (IllegalArgumentException e) {
                log.error("JSON 처리 중 오류 발생: 사용자 {}", dto.getUserId(), e);
                return ServiceResponse.error("JSON 처리 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            return ServiceResponse.error("보호자 매핑 업데이트 실패: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Integer> updateDoctorMapping(UserDto dto) {
        try {
            relationshipValidator.validate(dto);
            if (dto.getDoctorSeq() != 0 && dto.getDoctorId() != null && !dto.getDoctorId().equals("")) {
                int result = relationshipRepository.updateDoctorMapping(dto);
                return ServiceResponse.success(result);
            }
            return ServiceResponse.success(0);
        } catch (Exception e) {
            return ServiceResponse.error("의사 매핑 업데이트 실패: " + e.getMessage());
        }
    }
}

