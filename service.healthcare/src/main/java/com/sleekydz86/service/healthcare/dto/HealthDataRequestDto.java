package com.sleekydz86.service.healthcare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class HealthDataRequestDto {

    @NotEmpty(message = "사용자 ID는 필수입니다")
    @Size(max = 50, message = "사용자 ID는 50자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자 ID는 영문, 숫자, 언더스코어만 허용됩니다")
    private String userId;

    @NotNull(message = "데이터 타입은 필수입니다")
    @Pattern(regexp = "^[md]$", message = "데이터 타입은 'm' 또는 'd'만 허용됩니다")
    private String type;

    @NotNull(message = "데이터는 필수입니다")
    @Size(min = 1, max = 1000, message = "데이터는 1개 이상 1000개 이하여야 합니다")
    @Valid
    private List<HealthDataItemDto> data;
}

