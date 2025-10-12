package com.sleekydz86.web.user.dto;

import com.sleekydz86.web.common.vo.SearchVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends SearchVO {

    private static final long serialVersionUID = 7178145578197273851L;

    private int userSeq;

    private String userId;

    private String userPwEnc;

    private String userNm;

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
