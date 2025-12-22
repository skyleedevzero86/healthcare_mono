package com.sleekydz86.service.healthcare.validation;

import com.sleekydz86.service.healthcare.common.ValidationException;
import com.sleekydz86.service.healthcare.dto.MinuteDataDto;
import com.sleekydz86.service.healthcare.dto.MonthDayDataDto;
import com.sleekydz86.service.healthcare.dto.TestDto;

import java.util.Map;

public class HealthDataValidator {
    public void validate(MinuteDataDto dto) {
        if (dto == null) {
            throw new ValidationException("MinuteDataDto는 null일 수 없습니다");
        }
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new ValidationException("사용자 ID는 필수입니다");
        }
    }

    public void validate(MonthDayDataDto dto) {
        if (dto == null) {
            throw new ValidationException("MonthDayDataDto는 null일 수 없습니다");
        }
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new ValidationException("사용자 ID는 필수입니다");
        }
    }

    public void validate(TestDto dto) {
        if (dto == null) {
            throw new ValidationException("TestDto는 null일 수 없습니다");
        }
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new ValidationException("사용자 ID는 필수입니다");
        }
    }

    public void validate(Map<String, Object> params) {
        if (params == null) {
            throw new ValidationException("파라미터는 null일 수 없습니다");
        }
        if (params.get("userId") == null || params.get("userId").toString().trim().isEmpty()) {
            throw new ValidationException("사용자 ID는 필수입니다");
        }
    }
}
