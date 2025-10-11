package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@Entity(name = "health_community") //이클래스를 테이블과 매핑
public class Community {

    @Id
    @GeneratedValue
    @Column(name = "community_seq")//기본키에 매핑, @Access (AccessType.FIELD) 생략됨
    private int commuSeq;

    //@Column(length = 100000000)
    //@Lob // 대용량 데이터
    @Column(length = 100000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP) //날짜 시간
    private Date regDate;

    @Column()
    private int heartrate;

    @Column()
    private int temperature;

    @Column()
    private int bloodpress;

    @Column()
    private int smoking;

    @Column()
    private int drinking;

    @Column()
    private int exercise;

    @Column()
    private int age;

    @Column(name = "user_id")
    int userId;

    @Column
    String userNm;

    @Column
    int bodyAge;

    @PrePersist
    public void beforePersist() {
        this.regDate = new Date(); // 현재 시간을 설정합니다.
    }

}