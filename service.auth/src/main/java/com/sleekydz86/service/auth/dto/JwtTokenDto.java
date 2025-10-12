package com.sleekydz86.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtTokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
