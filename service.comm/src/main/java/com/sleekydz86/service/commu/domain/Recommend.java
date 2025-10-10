package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recommends")
@Data
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long recommendId;

    @Column(name = "community_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Community community; // 1:1 관계

    @Column(name = "user_seq")
    User user;
}