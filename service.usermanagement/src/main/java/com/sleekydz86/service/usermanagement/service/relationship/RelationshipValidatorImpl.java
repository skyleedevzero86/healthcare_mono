package com.sleekydz86.service.usermanagement.service.relationship;

import com.sleekydz86.service.usermanagement.common.ValidationException;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class RelationshipValidatorImpl implements RelationshipValidator {
    @Override
    public void validate(UserDto dto) {
        if (dto == null) {
            throw new ValidationException("사용자 정보는 null일 수 없습니다");
        }
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new ValidationException("사용자 ID는 필수입니다");
        }
    }
}

