package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BondProductRepository extends JpaRepository<BondProduct,Integer> {
    List<BondProduct> findTop5ByOrderByPriceDesc();

    @Query("SELECT b FROM BondProduct b JOIN b.bondProductPrice p WHERE p.isinCd IN :isinCds")
    List<BondProduct> findByBondProductPriceIsinCdIn(@Param("isinCds") List<String> isinCds);

}
