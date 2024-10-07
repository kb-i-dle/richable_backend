package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.AssetSummary;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;

public interface AssetSummaryRepository extends JpaRepository<AssetSummary, Integer> {
    AssetSummary findByUid(Optional<Member> uid);

    @Query(value = "SELECT AVG(a.total_amount) FROM asset.asset_summary a JOIN user_info.user_info u ON a.uid = u.uid WHERE u.birth_year BETWEEN :startYear AND :endYear", nativeQuery = true)
    long findAverageAmountByAgeRange(@Param("startYear") int startYear, @Param("endYear") int endYear);

    @Query(value = "SELECT * FROM asset.asset_summary WHERE uid = :uid ORDER BY update_date DESC LIMIT 1", nativeQuery = true)
    AssetSummary findLatestByUid(@Param("uid") Optional<Member> uid);

    AssetSummary findFirstByUidAndUpdateDateBeforeOrderByUpdateDateDesc(Optional<Member> uid, Date updateDate);
}
