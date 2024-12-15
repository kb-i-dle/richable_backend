package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.BondProductPrice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BondProductPriceRepository extends JpaRepository<BondProductPrice,Integer> {
    @Query("SELECT bpp FROM BondProductPrice bpp WHERE bpp.isinCd IS NOT NULL " +
            "AND bpp.isinCdNm IS NOT NULL " +
            "AND bpp.date IS NOT NULL " +
            "AND bpp.oneMonthAgoPrice IS NOT NULL " +
            "AND bpp.twoMonthsAgoPrice IS NOT NULL " +
            "AND bpp.threeMonthsAgoPrice IS NOT NULL " +
            "AND bpp.fourMonthsAgoPrice IS NOT NULL " +
            "AND bpp.fiveMonthsAgoPrice IS NOT NULL " +
            "AND bpp.sixMonthsAgoPrice IS NOT NULL")
    List<BondProductPrice> findAllNonNullValues();


}
