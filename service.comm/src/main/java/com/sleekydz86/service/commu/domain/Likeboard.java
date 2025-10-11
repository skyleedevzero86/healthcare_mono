package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Likeboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long likeId;

    @JoinColumn(name = "community_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Community commu; //1:1 관계

    @JoinColumn(name = "user_seq")
    @ManyToOne
    Usermng user;

}