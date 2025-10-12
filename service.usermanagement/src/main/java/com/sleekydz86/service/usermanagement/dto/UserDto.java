package com.sleekydz86.service.usermanagement.dto;

import com.sleekydz86.service.usermanagement.global.common.vo.SearchVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends SearchVO {

    /**
     *
     */
    private static final long serialVersionUID = -1207994115556874337L;

    private int userSeq;

    private String userId;

    private String userPwEnc;

    private String newUserPwEnc;

    private String userNm;

    private String userRoleFk;

    private String parentNm;

    private String doctorId;

    private int doctorSeq;

    private String guardian;

    private int guardianSeq;

    private String guardianId;

    private String birthEnc;

    private String telNumEnc;

    private String email;

    private String deptNm;

    private String height;

    private String weight;

    private String gender;

    private String bloodType;

    private ArrayList<Integer> guardianSeqArray;

    private String regDt;

    private String regId;

    private String uptDt;

    private String uptId;

    private boolean warning;

}
