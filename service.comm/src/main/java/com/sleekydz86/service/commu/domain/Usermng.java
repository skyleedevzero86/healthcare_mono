package com.sleekydz86.service.commu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity //이클래스를 테이블과 매핑
@Getter
@Table(name = "user_mng")
@RequiredArgsConstructor
public class Usermng {

    @Id
    @Column(name = "user_seq")
    final String userSeq;

    @Column
    final String userNm;

}