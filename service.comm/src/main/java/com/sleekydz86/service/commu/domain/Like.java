package com.sleekydz86.service.commu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long likeId;

    @Column(name = "commu_id")
    Long commuId; //1:1 관계

    @Column(name = "user_id")
    Long userId;

    int likeCnt;
}