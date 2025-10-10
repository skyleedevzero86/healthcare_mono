package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recommends")
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long recommendId;

    @ManyToOne
    @JoinColumn(name = "commu_id")
    Long commuId; // 1:1 관계

    @ManyToOne
    @JoinColumn(name = "user_id")
    Long userId;

    int recommendCnt;
}