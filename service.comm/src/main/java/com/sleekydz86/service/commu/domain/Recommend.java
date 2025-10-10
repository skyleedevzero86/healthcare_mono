package com.sleekydz86.service.commu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long RecommendId;

    @Column(name = "commu_id")
    Long commuId; //1:1 관계

    @Column(name = "user_id")
    Long userId;
    int recommendCnt;

}