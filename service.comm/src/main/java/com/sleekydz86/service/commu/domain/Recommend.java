package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;

@Table(name = "recommends")
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long recommendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    Usermng user;
}