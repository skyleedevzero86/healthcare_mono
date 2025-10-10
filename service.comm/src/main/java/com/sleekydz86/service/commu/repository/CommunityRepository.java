package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.domain.Community;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final EntityManager em;

    /**
     * 커뮤니티 보드 글쓰기
     * @param community
     */
    public void writeBoard(Community community) {
        em.persist(community);
