package com.idle.kb_i_dle_backend.member.repository;

import com.idle.kb_i_dle_backend.member.entity.UserApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

public interface UserApiRepository extends JpaRepository<UserApiEntity, Integer> {
    // Optional로 단일 객체 반환했음
    Optional<UserApiEntity> findByUid(Integer uid);
}
