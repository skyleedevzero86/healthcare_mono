package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Date;

@Entity
@Getter
public class community {

    @Id
    @GeneratedValue
    @Column(name = "commuId")
    private Long commuId;

    private String userNm;

    @Column(length = 100000000)
    private String content;
    private Date regDate;

    //내장타입
    @Embedded
    private diseaseCategory category;
}