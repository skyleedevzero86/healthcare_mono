package com.sleekydz86.service.usermanagement.service.relationship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.repository.UserRelationshipRepository;
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

    @Override
    public ServiceResponse<Integer> updateGuardianMapping(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            String str = dto.getGuardian();
            ObjectMapper obj = new ObjectMapper();
            try {
                int cnt = relationshipRepository.countSearchParent(dto);
                ArrayList<Integer> arr = new ArrayList<Integer>();
                if (str != null) {
                    result = obj.readValue(str, ArrayList.class);
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
            } catch (JsonProcessingException e) {
                log.error("JSON 처리 중 오류 발생: 사용자 {}", dto.getUserId(), e);
                return ServiceResponse.error("JSON processing failed: " + e.getMessage());
            }
        } catch (Exception e) {
            return ServiceResponse.error("Guardian mapping update failed: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Integer> updateDoctorMapping(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            if (dto.getDoctorSeq() != 0 && dto.getDoctorId() != null && !dto.getDoctorId().equals("")) {
                int result = relationshipRepository.updateDoctorMapping(dto);
                return ServiceResponse.success(result);
            }
            return ServiceResponse.success(0);
        } catch (Exception e) {
            return ServiceResponse.error("Doctor mapping update failed: " + e.getMessage());
        }
    }
}

