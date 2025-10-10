package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.domain.Community;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final EntityManager em;

    /**
     * 커뮤니티 보드 글쓰기
     *
     * @param community
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    public int writeBoard(Community community) {
        try {
            em.persist(community);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Community findBoard(Long id) {
        Community community = em.find(Community.class, id);
        return community;
    }

    public List<Community> findBoardList() {
        return em.createQuery("select c from Community c", Community.class)
                .getResultList();
    }
}