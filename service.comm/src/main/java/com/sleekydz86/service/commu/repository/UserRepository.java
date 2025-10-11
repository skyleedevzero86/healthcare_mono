package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.domain.Usermng;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public Usermng findOne(int userId) { return em.find(Usermng.class, userId);}
}