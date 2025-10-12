package com.sleekydz86.service.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindDto {

    @NotEmpty
    private String userRoleFk;

    @NotEmpty
    private String userNm;

    @NotEmpty
    private String telNumEnc;

    @NotEmpty
    private String birthEnc;

    private String userId;

    private String userPwEnc;
}
