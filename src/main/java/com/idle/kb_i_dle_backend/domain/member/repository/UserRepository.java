package com.idle.kb_i_dle_backend.domain.member.repository;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUid(Integer uid);

    Member findById(String id);



    Member findByEmail(String email);

    boolean existsById(String id);
}