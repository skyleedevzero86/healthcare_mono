package com.sleekydz86.service.commu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class Usermng {

    String userSeq;

    String user_id;

    String email;

    String user_pw_enc;

    String user_salt;

    String user_nm;

    String birth_enc;

    String tel_num_enc;

    String dept_nm;

    String height;

    String weight;

    String blood_type;

    String gender;

    Date reg_dt;

    String reg_id;

    Date upt_dt;

    String upt_id;

    String web_token;

    String mobile_token;

    String agreement_yn;

    String use_yn;

    String user_profile;

}


