package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BondRepository extends JpaRepository<Bond, Integer> {


    List<Bond> findAllByUidAndDeleteDateIsNull(Member uid);

    @Query(value = "SELECT CASE :monthsAgo " +
            "  WHEN 1 THEN blp.1m_b_price " +
            "  WHEN 2 THEN blp.2m_b_price " +
            "  WHEN 3 THEN blp.3m_b_price " +
            "  WHEN 4 THEN blp.4m_b_price " +
            "  WHEN 5 THEN blp.5m_b_price " +
            "  WHEN 6 THEN blp.6m_b_price " +
            "END AS closingPrice " +
            "FROM asset.bond b " +
            "JOIN product.bond_list_price blp  ON b.itms_nm = blp.isinCdNm " +
            "WHERE b.uid = :uid " +
            "AND b.delete_date IS NULL limit 1", nativeQuery = true)
    List<Bond> findAllByUidAndAddDateBefore(@Param("uid") Member uid, @Param("monthsAgo") int monthsAgo);


    @Query(value = "SELECT " +
            "CASE :monthsAgo " +
            "  WHEN 1 THEN blp.1m_b_price " +
            "  WHEN 2 THEN blp.2m_b_price " +
            "  WHEN 3 THEN blp.3m_b_price " +
            "  WHEN 4 THEN blp.4m_b_price " +
            "  WHEN 5 THEN blp.5m_b_price " +
            "  WHEN 6 THEN blp.6m_b_price " +
            "END AS closingPrice " +
            "FROM asset.bond b " +
            "JOIN product.bond_list_price blp ON b.itms_nm = blp.isinCdNm " +
            "WHERE b.itms_nm = :isinCdNm limit 1 ", nativeQuery = true)
    Double getBondPriceForMonth(@Param("isinCdNm") String isinCdNm, @Param("monthsAgo") int monthsAgo);

    List<Bond> findByUid(Member uid);

    @Query(value = "SELECT per_price FROM asset.bond WHERE itms_nm = :name ORDER BY add_date DESC LIMIT 1", nativeQuery = true)
    double getPriceByName(@Param("name") String name);

    // bond crud
    // 삭제되지 않은 금융 자산(Bond) 전체 조회
    List<Bond> findByUidAndDeleteDateIsNull(Member uid);

    // 특정 index값의 정보 조회
    Optional<Bond> findByIndexAndDeleteDateIsNull(@Param("index") Integer index);

    @Query(value =
            "SELECT month(b.add_date) as month, b.prod_category as category, SUM(b.per_price * b.cnt) as totalAmount " +
                    "FROM asset.bond b WHERE b.uid = :uid " +
                    "GROUP BY month(b.add_date), b.prod_category", nativeQuery = true)
    List<Object[]> findMonthlyBondAssets(@Param("uid") Member uid);

    @Query("SELECT bp FROM BondProduct bp WHERE bp.isinCdNm = :isinCdNm")
    BondProduct findByIsinCdNm(@Param("isinCdNm") String isinCdNm);

}
