package com.idle.kb_i_dle_backend.member.repository;

import com.idle.kb_i_dle_backend.member.entity.UserBadgeEntity;
import com.idle.kb_i_dle_backend.member.entity.UserBadgeEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, UserBadgeEntityPK> {
    // uid로 뱃지 목록을 조회하는거 만듬었음
    List<UserBadgeEntity> findByUid(Integer uid);
}
