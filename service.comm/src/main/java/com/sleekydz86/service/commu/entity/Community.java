package com.sleekydz86.service.commu.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Community {

    private int commuSeq;

    private String content;

    private Date regDate;

    private int heartrate;

    private double temperature;

    private double bloodpress;

    private int smoking;

    private int drinking;

    private int exercise;

    private int age;

    String userId;

    String userNm;

    int bodyAge;

    String userSeq;

    DiseaseCategory category;

    public void beforePersist() {
        this.regDate = new Date();
    }

    public int getCommuId() {
        return commuSeq;
    }

}

