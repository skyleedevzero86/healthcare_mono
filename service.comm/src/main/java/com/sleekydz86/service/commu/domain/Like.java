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
    @JoinColumn(name = "communityId")
    Community community; // 1:1 관계

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;
}