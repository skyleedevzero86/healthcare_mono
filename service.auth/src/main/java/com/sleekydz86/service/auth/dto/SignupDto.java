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
public class SignupDto {

    private int userSeq;

    @NotEmpty
    @Size(max=30)
    private String userId;

    @NotEmpty
    private String userPwEnc;

    @NotEmpty
    private String userNm;

    @NotEmpty
    private String userRoleFk;

    @NotEmpty
    private String birthEnc;

    @NotEmpty
    private String telNumEnc;

    @NotEmpty
    private String email;

    private String deptNm;

    private int doctorSeq;

    private String doctorId;

    private String guardian;

    private int guardianSeq;

    private String guardianId;

    private float height;

    private float weight;

    private String gender;

    private String bloodType;

    private String imgFileName;
    private String agreementYn;
    private String userYn;

}
