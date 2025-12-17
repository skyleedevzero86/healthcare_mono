package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.domain.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    @PersistenceContext
    private EntityManager em;

    public int writeBoard(Community community) {
        try {
            em.persist(community);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Community findBoard(int commuSeq) {
        Community community = em.find(Community.class, commuSeq);
        return community;
    }

    public List<Community> findBoardList(Map<String, Object> map) {
        String jpql = "select c from Community c";

        if (map.containsKey("age") && map.get("age") != null) {
            try {
                int age = Integer.parseInt(map.get("age").toString());
                int ageAvg = age / 10;
                jpql += " where c.age/10 = :ageAvg";
                return em.createQuery(jpql, Community.class)
                        .setParameter("ageAvg", ageAvg)
                        .getResultList();
            } catch (NumberFormatException e) {
            }
        }

        int pageIdx = map.containsKey("pageIdx") && map.get("pageIdx") != null
                ? Integer.parseInt(map.get("pageIdx").toString())
                : 0;
        int pageSize = map.containsKey("pageSize") && map.get("pageSize") != null
                ? Integer.parseInt(map.get("pageSize").toString())
                : 10;

        jpql += " order by c.regDate desc";

        return em.createQuery(jpql, Community.class)
                .setFirstResult(pageIdx * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public int updateBoard(Community community) {
        try {
            Community existing = em.find(Community.class, community.getCommuSeq());
            if (existing == null) {
                return 0;
            }
            existing.setContent(community.getContent());
            existing.setHeartrate(community.getHeartrate());
            existing.setTemperature(community.getTemperature());
            existing.setBloodpress(community.getBloodpress());
            existing.setSmoking(community.getSmoking());
            existing.setDrinking(community.getDrinking());
            existing.setExercise(community.getExercise());
            existing.setAge(community.getAge());
            existing.setBodyAge(community.getBodyAge());
            em.merge(existing);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int deleteBoard(int commuSeq) {
        try {
            Community community = em.find(Community.class, commuSeq);
            if (community == null) {
                return 0;
            }
            em.remove(community);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}