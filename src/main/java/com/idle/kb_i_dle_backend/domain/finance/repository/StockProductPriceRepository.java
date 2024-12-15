package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.StockProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockProductPriceRepository extends JpaRepository<StockProductPrice, Integer> {

    StockProductPrice findByShortCode(String shortCode);
}
