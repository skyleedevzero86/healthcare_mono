package com.sleekydz86.service.healthcare.validation;

import com.sleekydz86.service.healthcare.dto.HealthDataItemDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HealthDataValidator implements ConstraintValidator<ValidHealthData, HealthDataItemDto> {

    @Override
    public void initialize(ValidHealthData constraintAnnotation) {
    }

    @Override
    public boolean isValid(HealthDataItemDto data, ConstraintValidatorContext context) {
        if (data == null) {
            return true;
        }

        boolean isValid = true;

        if (data.getHeartrate() != null) {
            if (data.getHeartrate() < 0 || data.getHeartrate() > 300) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("심박수는 0 이상 300 이하여야 합니다")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        if (data.getTemperature() != null) {
            if (data.getTemperature() < 30.0f || data.getTemperature() > 45.0f) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("체온은 30도 이상 45도 이하여야 합니다")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        if (data.getSpo2() != null) {
            if (data.getSpo2() < 0 || data.getSpo2() > 100) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("산소포화도는 0 이상 100 이하여야 합니다")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        if (data.getBloodpressMin() != null && data.getBloodpressMax() != null) {
            if (data.getBloodpressMin() > data.getBloodpressMax()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("최저혈압은 최고혈압보다 작아야 합니다")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}

