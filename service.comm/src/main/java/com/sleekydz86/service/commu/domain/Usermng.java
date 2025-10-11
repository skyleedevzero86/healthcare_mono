package com.sleekydz86.service.commu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Date;

@Entity //이클래스를 테이블과 매핑
@Table(name = "user_mng")
@Getter
@RequiredArgsConstructor
public class Usermng {

    @Id
    @Column(name = "user_seq")
    final String userSeq;

    @Column
    final String user_id;

    @Column
    final String email;

    @Column
    final String user_pw_enc;

    @Column
    final String user_salt;

    @Column
    final String user_nm;

    @Column
    final String birth_enc;

    @Column
    final String tel_num_enc;

    @Column
    final String dept_nm;

    @Column
    final String height;

    @Column
    final String weight;

    @Column
    final String blood_type;

    @Column
    final String gender;

    @Column
    final Date reg_dt;

    @Column
    final String reg_id;

    @Column
    final Date upt_dt;

    @Column
    final String upt_id;

    @Column
    final String web_token;

    @Column
    final String mobile_token;

    @Column
    final String agreement_yn;

    @Column
    final String use_yn;

    @Column
    final String user_profile;


}