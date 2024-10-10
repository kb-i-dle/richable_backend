package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.StockProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockProductRepository extends JpaRepository<StockProduct, Integer> {
    StockProduct findByStandardCode(String standardCode);
    StockProduct findByShortCode(String shortCode);

}
