package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @Query(value = "WITH latest_dates AS (\n" +
            "    SELECT DISTINCT date\n" +
            "    FROM product.stock_price\n" +
            "    ORDER BY date DESC\n" +
            "    LIMIT 2\n" +
            "), price_diff AS (\n" +
            "    SELECT sp.standard_code, sp.stock_nm,\n" +
            "           MAX(CASE WHEN sp.date = ld.latest_date THEN sp.price END) AS latest_price,\n" +
            "           MAX(CASE WHEN sp.date = ld.previous_date THEN sp.price END) AS previous_price\n" +
            "    FROM product.stock_price sp\n" +
            "    CROSS JOIN (\n" +
            "        SELECT MAX(date) AS latest_date, MIN(date) AS previous_date\n" +
            "        FROM latest_dates\n" +
            "    ) ld\n" +
            "    WHERE sp.date IN (SELECT date FROM latest_dates)\n" +
            "    GROUP BY sp.standard_code, sp.stock_nm\n" +
            ")\n" +
            "SELECT pd.standard_code, pd.stock_nm, \n" +
            "       COALESCE(pd.latest_price, 0) - COALESCE(pd.previous_price, 0) AS price_difference,\n" +
            "       COALESCE(pd.previous_price, 0) AS previous_price, \n" +
            "       COALESCE(pd.latest_price, 0) AS latest_price\n" +
            "FROM price_diff pd\n" +
            "WHERE pd.latest_price IS NOT NULL AND pd.previous_price IS NOT NULL\n" +
            "ORDER BY price_difference DESC",
            nativeQuery = true)
    List<Object[]> findPriceDifferenceBetweenLastTwoDates();

    // 모든 주식 정보 조회
    @Query("SELECT s FROM StockPrice s WHERE s.date = (SELECT MAX(sp.date) FROM StockPrice sp)")
    List<StockPrice> findAllLatestStockInfo();

    // 특정 주식의 가격 업데이트
    @Modifying
    @Query("UPDATE StockPrice s SET s.price = :price WHERE s.standard_code = :standardCode AND s.date = :date")
    void updateStockPrice(@Param("price") Integer price, @Param("standardCode") String standardCode, @Param("date") Date date);


}
