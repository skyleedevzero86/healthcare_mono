package com.sleekydz86.service.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int userSeq;

    private String userId;

    private String userNm;

    private String userPwEnc;

    private String userRoleFk;

    private String birthEnc;

    private String telNumEnc;

    private String email;

    private String deptNm;

    private String regDt;

    private String regId;

    private String uptDt;

    private String uptId;

}
