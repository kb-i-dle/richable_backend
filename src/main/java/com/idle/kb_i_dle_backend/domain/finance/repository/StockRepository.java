package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockProduct;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    //선택한 주식의 종가
    @Query(value = "SELECT sl.price " +
            "FROM asset.stock s " +
            "JOIN product.stock_list sl ON CAST(sl.short_code AS UNSIGNED) = s.pdno " +
            "WHERE s.pdno = :pdno LIMIT 1", nativeQuery = true)
    double getStockPriceByPdno(@Param("pdno") int pdno);

    //uid가 일치하는 주식의 수량과 종가
    @Query(value = "SELECT s.hldg_qty, sl.price " +
            "FROM asset.stock s " +
            "JOIN product.stock_list sl ON CAST(sl.short_code AS UNSIGNED) = s.pdno " +
            "WHERE s.uid = :uid", nativeQuery = true)
    List<Object[]> getStockBalanceAndPrice(@Param("uid") int uid);

    List<Stock> findAllByUidAndDeleteDateIsNull(Member uid);

    // 선택한 주식의 해당 시점의 가격을 가져옴
    @Query(value = "SELECT " +
            "CASE :monthsAgo " +
            "  WHEN 1 THEN slp.1m_b_price " +
            "  WHEN 2 THEN slp.2m_b_price " +
            "  WHEN 3 THEN slp.3m_b_price " +
            "  WHEN 4 THEN slp.4m_b_price " +
            "  WHEN 5 THEN slp.5m_b_price " +
            "  WHEN 6 THEN slp.6m_b_price " +
            "END " +
            "FROM product.stock_list sl " +
            "JOIN product.stock_list_price slp ON sl.standard_code = slp.standardCode " +
            "WHERE sl.short_code = :stockId", nativeQuery = true)
    Double getStockPriceForMonth(
            @Param("stockId") int stockId,
            @Param("monthsAgo") int monthsAgo);


    @Query(value = "SELECT s.hldg_qty, " +
            "CASE :monthsAgo " +
            "  WHEN 1 THEN slp.1m_b_price " +
            "  WHEN 2 THEN slp.2m_b_price " +
            "  WHEN 3 THEN slp.3m_b_price " +
            "  WHEN 4 THEN slp.4m_b_price " +
            "  WHEN 5 THEN slp.5m_b_price " +
            "  WHEN 6 THEN slp.6m_b_price " +
            "END " +
            "FROM asset.stock s " +
            "JOIN product.stock_list sl ON CAST(s.pdno AS CHAR) = sl.short_code " +  // pdno를 String으로 변환하여 비교
            "JOIN product.stock_list_price slp ON sl.standard_code = slp.standardCode " +
            "WHERE s.uid = :uid " +
            "AND s.delete_date IS NULL", nativeQuery = true)
    List<Object[]> getStockBalanceAndClosingPriceBefore(@Param("uid") Member uid,
                                                        @Param("monthsAgo") int monthsAgo);

    List<Stock> findByUid(Member uid);

    @Query("SELECT sl FROM StockProduct sl WHERE sl.price IS NOT NULL ORDER BY sl.price DESC")
    List<StockProduct> findTop5StocksByPrice();

    // 이 메서드는 StockList 엔티티를 대상으로 하는 별도의 쿼리를 사용합니다.
    @Query("SELECT sl FROM StockProduct sl WHERE sl.price IS NOT NULL ORDER BY sl.price DESC")
    List<StockProduct> findTop5ByOrderByPriceDesc();

    List<Stock> findByUidAndDeleteDateIsNull(Member tempUser);

    Optional<Object> findByIndexAndDeleteDateIsNull(Integer index);

    @Query(value =
            "SELECT month(s.add_date) as month, s.prod_category as category, SUM(s.avg_buy_price * s.hldg_qty) as totalAmount "
                    +
                    "FROM asset.stock s WHERE s.uid = :uid " +
                    "GROUP BY month(s.add_date), s.prod_category", nativeQuery = true)
    List<Object[]> findMonthlyStockAssets(@Param("uid") Member uid);

    Stock findByPdno(String pdno);
}
