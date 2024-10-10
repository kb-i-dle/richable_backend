package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinProductPriceRepository extends JpaRepository<CoinProductPrice, Integer> {

}
