package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.domain.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    @PersistenceContext //SPRINGDL ENtitymanager만들어서 주입해줌 @AutoWired로 변경가능-> REquiredArgsConstruct로 변경가능
    private EntityManager em;

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
        return em.createQuery("select c from health_community c", Community.class)
                .getResultList();
    }
}