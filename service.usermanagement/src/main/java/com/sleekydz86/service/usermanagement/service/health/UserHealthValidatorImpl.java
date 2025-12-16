package com.sleekydz86.service.usermanagement.service.health;

import com.sleekydz86.service.usermanagement.common.ValidationException;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import org.springframework.stereotype.Component;

@Component
public class UserHealthValidatorImpl implements UserHealthValidator {
    @Override
    public void validate(UserhealthDto dto) {
        if (dto == null) {
            throw new ValidationException("사용자 건강 정보는 null일 수 없습니다");
        }
        if (dto.getAgeRange() == null || dto.getAgeRange().trim().isEmpty()) {
            throw new ValidationException("연령대는 필수입니다");
        }
        if (dto.getGender() == null || dto.getGender().trim().isEmpty()) {
            throw new ValidationException("성별은 필수입니다");
        }
    }
}

