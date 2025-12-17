package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

