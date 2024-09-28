package com.idle.kb_i_dle_backend.member.repository;

import com.idle.kb_i_dle_backend.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<User, String> {
    // 닉네임을 통해 유저 정보를 조회 메서드 만듬
    User findByUid(User Uid);
}
