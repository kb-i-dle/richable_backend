package com.idle.kb_i_dle_backend.finance.repository;

import com.idle.kb_i_dle_backend.finance.entity.Income;
import com.idle.kb_i_dle_backend.finance.entity.OutComeUser;
import com.idle.kb_i_dle_backend.member.entity.User;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutComeUserRepository extends JpaRepository<OutComeUser, Integer> {

    List<OutComeUser> findAllByUid(int uid);

    List<OutComeUser> findByUidAndDateBetween(int uid, Date startDate, Date endDate);
}
