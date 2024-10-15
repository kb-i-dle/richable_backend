package com.idle.kb_i_dle_backend.domain.member.repository;


import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberBadge;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberBadgeId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge, MemberBadgeId> {
    List<MemberBadge> findByUidOrderByMainDesc(Member uid);

    List<MemberBadge> findByUidAndAchiveOrderByMain(Member uid, boolean badgeAchive);
}
