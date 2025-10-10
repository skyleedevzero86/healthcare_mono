package com.sleekydz86.service.commu;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class JPATest {

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    public void 게시판쓰기() throws Exception {
        System.out.println("===== JPA 테스트 시작 =====");

        //given
        em.clear();
        System.out.println("1. 테스트 데이터 준비 중...");

        User user = new User();
        user.setUserNm("ash");
        em.persist(user);
        System.out.println("사용자 생성 완료: " + user.getUserNm() + " (ID: " + user.getUserId() + ")");

        Community commu = new Community();
        commu.setUser(user);
        commu.setContent("This is test board");
        commu.setUserNm("ash"); // Community의 userNm 필드도 설정
        System.out.println("커뮤니티 게시글 생성 완료: " + commu.getContent());

        //when
        em.persist(commu);
        em.flush();
        System.out.println("데이터베이스에 저장 완료!");

        //then
        System.out.println("2. 검증 시작...");

        Community savedCommunity = em.find(Community.class, commu.getCommuId());
        assertNotNull(savedCommunity, "저장된 커뮤니티 게시글이 null입니다!");
        assertEquals("This is test board", savedCommunity.getContent(), "게시글 내용이 일치하지 않습니다!");
        assertEquals("ash", savedCommunity.getUserNm(), "사용자명이 일치하지 않습니다!");
        assertNotNull(savedCommunity.getUser(), "연관된 사용자 정보가 null입니다!");
        assertEquals("ash", savedCommunity.getUser().getUserNm(), "연관된 사용자명이 일치하지 않습니다!");

        System.out.println("모든 검증 통과!");
        System.out.println("저장된 데이터 정보:");
        System.out.println("   - 게시글 ID: " + savedCommunity.getCommuId());
        System.out.println("   - 게시글 내용: " + savedCommunity.getContent());
        System.out.println("   - 작성자: " + savedCommunity.getUserNm());
        System.out.println("   - 사용자 ID: " + savedCommunity.getUser().getUserId());
        System.out.println("   - 등록일: " + savedCommunity.getRegDate());

        System.out.println("===== JPA 테스트 성공 완료 =====");
    }

    @Test
    @Transactional
    public void 게시판목록조회() throws Exception {
        System.out.println("===== 게시판 목록 조회 테스트 시작 =====");

        //given
        em.clear();
        System.out.println("테스트 데이터 생성 중...");

        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUserNm("user" + i);
            em.persist(user);

            Community commu = new Community();
            commu.setUser(user);
            commu.setUserNm("user" + i);
            commu.setContent("Test board content " + i);
            em.persist(commu);
        }
        em.flush();
        System.out.println("3개의 테스트 게시글 생성 완료!");

        //when
        TypedQuery<Community> query = em.createQuery("select c from Community c", Community.class);
        List<Community> communities = query.getResultList();
        System.out.println("데이터베이스에서 게시글 목록 조회 완료!");

        //then
        System.out.println("검증 시작...");
        assertNotNull(communities, "조회된 게시글 목록이 null입니다!");
        assertEquals(3, communities.size(), "조회된 게시글 개수가 예상과 다릅니다!");

        System.out.println("모든 검증 통과!");
        System.out.println("조회된 게시글 목록:");
        for (int i = 0; i < communities.size(); i++) {
            Community commu = communities.get(i);
            System.out.println("   " + (i + 1) + ". ID: " + commu.getCommuId() +
                    ", 작성자: " + commu.getUserNm() +
                    ", 내용: " + commu.getContent());
        }

        System.out.println("===== 게시판 목록 조회 테스트 성공 완료 =====");
    }

    @Test
    @Transactional
    public void 특정게시글조회() throws Exception {
        System.out.println("===== 특정 게시글 조회 테스트 시작 =====");

        //given
        em.clear();
        System.out.println("테스트 데이터 생성 중...");

        User user = new User();
        user.setUserNm("testUser");
        em.persist(user);

        Community commu = new Community();
        commu.setUser(user);
        commu.setUserNm("testUser");
        commu.setContent("Specific test content");
        em.persist(commu);
        em.flush();

        Long savedId = commu.getCommuId();
        System.out.println("테스트 게시글 생성 완료! ID: " + savedId);

        //when
        Community foundCommunity = em.find(Community.class, savedId);
        System.out.println("ID로 게시글 조회 완료!");

        //then
        System.out.println("검증 시작...");
        assertNotNull(foundCommunity, "조회된 게시글이 null입니다!");
        assertEquals(savedId, foundCommunity.getCommuId(), "조회된 게시글 ID가 일치하지 않습니다!");
        assertEquals("Specific test content", foundCommunity.getContent(), "게시글 내용이 일치하지 않습니다!");
        assertEquals("testUser", foundCommunity.getUserNm(), "작성자명이 일치하지 않습니다!");

        System.out.println("모든 검증 통과!");
        System.out.println("조회된 게시글 정보:");
        System.out.println("   - 게시글 ID: " + foundCommunity.getCommuId());
        System.out.println("   - 작성자: " + foundCommunity.getUserNm());
        System.out.println("   - 내용: " + foundCommunity.getContent());
        System.out.println("   - 등록일: " + foundCommunity.getRegDate());

        System.out.println("===== 특정 게시글 조회 테스트 성공 완료 =====");
    }
}