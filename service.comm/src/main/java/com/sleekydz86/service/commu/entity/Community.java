package com.sleekydz86.service.commu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "health_community")
@Getter
@Setter
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_seq")
    private int commuSeq;

    @Column(length = 100000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_date")
    private Date regDate;

    @Column()
    private int heartrate;

    @Column()
    private double temperature;

    @Column()
    private double bloodpress;

    @Column()
    private int smoking;

    @Column()
    private int drinking;

    @Column()
    private int exercise;

    @Column()
    private int age;

    @Column(name = "user_id")
    String userId;

    @Column
    String userNm;

    @Column(name = "body_age")
    int bodyAge;

    @PrePersist
    public void beforePersist() {
        this.regDate = new Date();
    }

}
