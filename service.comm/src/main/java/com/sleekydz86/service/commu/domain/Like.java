package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long likeId;

    @ManyToOne
    @JoinColumn(name = "commu_id")
    Long commuId; // 1:1 관계

    @ManyToOne
    @JoinColumn(name = "user_id")
    Long userId;

    int likeCnt;
}