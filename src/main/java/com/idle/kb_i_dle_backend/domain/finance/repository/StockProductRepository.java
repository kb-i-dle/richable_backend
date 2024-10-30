package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.StockProduct;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockProductRepository extends JpaRepository<StockProduct, Integer> {
    StockProduct findByStandardCode(String standardCode);
    StockProduct findByShortCode(String shortCode);

    @Query("SELECT s FROM StockProduct s WHERE s.price IS NOT NULL ORDER BY s.price DESC")
    List<StockProduct> findOrderByPriceDesc();

    @Query("SELECT s FROM StockProduct s WHERE s.price IS NOT NULL ORDER BY s.price DESC")
    Page<StockProduct> findStockProductsWithPaging(Pageable pageable);

}
