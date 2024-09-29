package com.idle.kb_i_dle_backend.finance.repository;

import com.idle.kb_i_dle_backend.finance.entity.Bond;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BondRepository extends JpaRepository<Bond, Integer> {


    List<Bond> findAllByUidAndDeleteDateIsNull(int uid);

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
            "WHERE b.uid = :uid AND b.add_date <= :endDate " +
            "AND b.delete_date IS NULL", nativeQuery = true)
    List<Bond> findAllByUidAndAddDateBefore(@Param("uid")int uid, @Param("endDate")Date endDate,@Param("monthsAgo") int monthsAgo);


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
            "WHERE b.itms_nm = :isinCdNm AND b.add_date <= :endDate", nativeQuery = true)
    Double getBondPriceForMonth(@Param("isinCdNm") String isinCdNm, @Param("endDate")Date endDate,@Param("monthsAgo") int monthsAgo);

}
