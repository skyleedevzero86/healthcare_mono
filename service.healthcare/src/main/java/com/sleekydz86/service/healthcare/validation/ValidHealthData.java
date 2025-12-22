package com.sleekydz86.service.healthcare.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = HealthDataValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHealthData {
    String message() default "유효하지 않은 건강 데이터입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

