package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "likes")
@Data
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