package com.sleekydz86.service.commu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usermng {

    String userSeq;

    String user_id;

    final String email;

    final String user_pw_enc;

    final String user_salt;

    final String user_nm;

    final String birth_enc;

    final String tel_num_enc;

    final String dept_nm;

    final String height;

    final String weight;

    final String blood_type;

    final String gender;

    final Date reg_dt;

    final String reg_id;

    final Date upt_dt;

    final String upt_id;

    final String web_token;

    final String mobile_token;

    final String agreement_yn;

    final String use_yn;

    final String user_profile;

}


