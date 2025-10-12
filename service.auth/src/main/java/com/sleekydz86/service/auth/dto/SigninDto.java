package com.sleekydz86.service.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninDto {

    @NotEmpty @Size(max=30)
    private String userId;

    @NotEmpty
    private String userPwEnc;

    private String userRoleFk;

    @NotEmpty
    private String source;
}
