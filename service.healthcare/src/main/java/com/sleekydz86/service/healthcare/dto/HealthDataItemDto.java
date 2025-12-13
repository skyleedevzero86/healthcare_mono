package com.sleekydz86.service.healthcare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HealthDataItemDto {

    @Min(value = 0, message = "심박수는 0 이상이어야 합니다")
    @Max(value = 300, message = "심박수는 300 이하여야 합니다")
    private Integer heartrate;

    @Min(value = 30, message = "체온은 30도 이상이어야 합니다")
    @Max(value = 45, message = "체온은 45도 이하여야 합니다")
    private Float temperature;

    @Min(value = 0, message = "산소포화도는 0 이상이어야 합니다")
    @Max(value = 100, message = "산소포화도는 100 이하여야 합니다")
    private Integer spo2;

    @Min(value = 0, message = "걸음수는 0 이상이어야 합니다")
    @Max(value = 100000, message = "걸음수는 100000 이하여야 합니다")
    private Integer step;

    @Min(value = 0, message = "스트레스는 0 이상이어야 합니다")
    @Max(value = 100, message = "스트레스는 100 이하여야 합니다")
    private Integer stress;

    @Min(value = 0, message = "최저혈압은 0 이상이어야 합니다")
    @Max(value = 200, message = "최저혈압은 200 이하여야 합니다")
    private Integer bloodpressMin;

    @Min(value = 0, message = "최고혈압은 0 이상이어야 합니다")
    @Max(value = 300, message = "최고혈압은 300 이하여야 합니다")
    private Integer bloodpressMax;

    @Min(value = 0, message = "호흡수는 0 이상이어야 합니다")
    @Max(value = 60, message = "호흡수는 60 이하여야 합니다")
    private Integer repiratory;

    @Min(value = 0, message = "수면시간은 0 이상이어야 합니다")
    @Max(value = 1440, message = "수면시간은 1440분(24시간) 이하여야 합니다")
    private Integer sleep;

    @Pattern(regexp = "^\\d{8}$", message = "시간 형식은 YYYYMMDDHHmmss 형식이어야 합니다")
    private String time;
}

