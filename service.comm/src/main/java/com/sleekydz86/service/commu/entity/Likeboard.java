package com.sleekydz86.service.commu.entity;

import jakarta.persistence.*;

public class Likeboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long likeId;

    @JoinColumn(name = "community_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Community commu;

    @JoinColumn(name = "user_seq")
    @ManyToOne
    Usermng user;

}

