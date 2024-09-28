package com.idle.kb_i_dle_backend.member.repository;

import com.idle.kb_i_dle_backend.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.idle.kb_i_dle_backend.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByNickname(String nickname);
    //추가
    Optional<User> findByUid(Integer uid);
}

