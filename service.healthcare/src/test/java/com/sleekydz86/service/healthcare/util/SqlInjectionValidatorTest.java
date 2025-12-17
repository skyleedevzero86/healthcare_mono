package com.sleekydz86.service.healthcare.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SqlInjectionValidator 단위 테스트")
class SqlInjectionValidatorTest {

    @Test
    @DisplayName("유효한 컬럼명 검증 성공")
    void isValidColumnName_Valid() {
        assertThat(SqlInjectionValidator.isValidColumnName("heartrate")).isTrue();
        assertThat(SqlInjectionValidator.isValidColumnName("temperature")).isTrue();
        assertThat(SqlInjectionValidator.isValidColumnName("spo2")).isTrue();
        assertThat(SqlInjectionValidator.isValidColumnName("stress")).isTrue();
    }

    @Test
    @DisplayName("컬럼명 검증 실패 - null")
    void isValidColumnName_Null() {
        assertThat(SqlInjectionValidator.isValidColumnName(null)).isFalse();
    }

    @Test
    @DisplayName("컬럼명 검증 실패 - 빈 문자열")
    void isValidColumnName_Empty() {
        assertThat(SqlInjectionValidator.isValidColumnName("")).isFalse();
    }

    @Test
    @DisplayName("컬럼명 검증 실패 - 허용되지 않은 컬럼명")
    void isValidColumnName_NotAllowed() {
        assertThat(SqlInjectionValidator.isValidColumnName("invalid_column")).isFalse();
        assertThat(SqlInjectionValidator.isValidColumnName("DROP TABLE")).isFalse();
    }

    @Test
    @DisplayName("컬럼명 검증 실패 - SQL Injection 시도")
    void isValidColumnName_SqlInjection() {
        assertThat(SqlInjectionValidator.isValidColumnName("'; DROP TABLE users; --")).isFalse();
        assertThat(SqlInjectionValidator.isValidColumnName("1 OR 1=1")).isFalse();
    }

    @Test
    @DisplayName("컬럼명 정제 성공")
    void sanitizeColumnName_Success() {
        String result = SqlInjectionValidator.sanitizeColumnName("HEARTRATE");

        assertThat(result).isEqualTo("heartrate");
    }

    @Test
    @DisplayName("컬럼명 정제 실패")
    void sanitizeColumnName_Failure() {
        assertThatThrownBy(() -> SqlInjectionValidator.sanitizeColumnName("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 컬럼명");
    }

    @Test
    @DisplayName("유효한 조건값 검증 성공")
    void isValidCondition_Valid() {
        assertThat(SqlInjectionValidator.isValidCondition("1")).isTrue();
        assertThat(SqlInjectionValidator.isValidCondition("30")).isTrue();
        assertThat(SqlInjectionValidator.isValidCondition("60")).isTrue();
    }

    @Test
    @DisplayName("조건값 검증 실패 - null")
    void isValidCondition_Null() {
        assertThat(SqlInjectionValidator.isValidCondition(null)).isFalse();
    }

    @Test
    @DisplayName("조건값 검증 실패 - 빈 문자열")
    void isValidCondition_Empty() {
        assertThat(SqlInjectionValidator.isValidCondition("")).isFalse();
    }

    @Test
    @DisplayName("조건값 검증 실패 - 범위 초과")
    void isValidCondition_OutOfRange() {
        assertThat(SqlInjectionValidator.isValidCondition("0")).isFalse();
        assertThat(SqlInjectionValidator.isValidCondition("61")).isFalse();
        assertThat(SqlInjectionValidator.isValidCondition("100")).isFalse();
    }

    @Test
    @DisplayName("조건값 검증 실패 - 숫자가 아님")
    void isValidCondition_NotNumber() {
        assertThat(SqlInjectionValidator.isValidCondition("abc")).isFalse();
        assertThat(SqlInjectionValidator.isValidCondition("1.5")).isFalse();
        assertThat(SqlInjectionValidator.isValidCondition("-5")).isFalse();
    }

    @Test
    @DisplayName("조건값 정제 성공")
    void sanitizeCondition_Success() {
        String result = SqlInjectionValidator.sanitizeCondition("30");

        assertThat(result).isEqualTo("30");
    }

    @Test
    @DisplayName("조건값 정제 실패")
    void sanitizeCondition_Failure() {
        assertThatThrownBy(() -> SqlInjectionValidator.sanitizeCondition("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 조건값");
    }
}

