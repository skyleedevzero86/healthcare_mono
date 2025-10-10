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

    @ManyToOne
    @JoinColumn(name = "community_Id")
    Community community; // 1:1 관계

    @ManyToOne
    @JoinColumn(name = "user_Id")
    User user;
}