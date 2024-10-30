package com.idle.kb_i_dle_backend.domain.finance.repository;

import com.idle.kb_i_dle_backend.domain.finance.entity.AssetSummary;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AssetSummaryRepository extends JpaRepository<AssetSummary, Integer> {
    AssetSummary findByUid(Optional<Member> uid);

    @Query(value = "SELECT AVG(a.total_amount) FROM asset.asset_summary a JOIN user_info.user_info u ON a.uid = u.uid WHERE u.birth_year BETWEEN :startYear AND :endYear", nativeQuery = true)
    Long findAverageAmountByAgeRange(@Param("startYear") int startYear, @Param("endYear") int endYear);

    @Query(value = """
        SELECT AVG(CASE 
            WHEN :category = 'bond' THEN a.bond
            WHEN :category = 'deposit' THEN a.deposit
            WHEN :category = 'saving' THEN a.saving
            WHEN :category = 'subscription' THEN a.subscription
            WHEN :category = 'withdrawal' THEN a.withdrawal
            WHEN :category = 'cash' THEN a.cash
            WHEN :category = 'stock' THEN a.stock
            WHEN :category = 'coin' THEN a.coin
        END) 
        FROM asset.asset_summary a 
        JOIN user_info.user_info u ON a.uid = u.uid 
        WHERE u.birth_year BETWEEN :startYear AND :endYear
    """, nativeQuery = true)
    Long findAverageAmountByAgeRangeAndCategory(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear,
            @Param("category") String category
    );

    @Query(value = "SELECT * FROM asset.asset_summary WHERE uid = :uid ORDER BY update_date DESC LIMIT 1", nativeQuery = true)
    AssetSummary findLatestByUid(@Param("uid") Member uid);

    AssetSummary findFirstByUidAndUpdateDateBeforeOrderByUpdateDateDesc(Member uid, Date updateDate);

    // 최신 것을 가져오는 쿼리
    @Query("SELECT a FROM AssetSummary a WHERE a.uid = :uid AND a.updateDate = (SELECT MAX(b.updateDate) FROM AssetSummary b WHERE b.uid = :uid)")
    AssetSummary findLatestByUidZeroMonthAgo(@Param("uid") Member uid);

    @Query("SELECT a FROM AssetSummary a WHERE a.uid = :uid AND a.updateDate = (SELECT MAX(b.updateDate) FROM AssetSummary b WHERE b.uid = :uid AND b.updateDate <= :date)")
    AssetSummary findLatestByUidAndDateBefore(@Param("uid") Member uid, @Param("date") Date date);

    // 다음 메서드들은 findLatestByUidAndDateBefore를 사용하도록 변경
    default AssetSummary findLatestByUidOneMonthAgo(@Param("uid") Member uid, @Param("oneMonthAgo") Date oneMonthAgo) {
        return findLatestByUidAndDateBefore(uid, oneMonthAgo);
    }

    default AssetSummary findLatestByUidTwoMonthAgo(@Param("uid") Member uid, @Param("twoMonthsAgo") Date twoMonthsAgo) {
        return findLatestByUidAndDateBefore(uid, twoMonthsAgo);
    }

    default AssetSummary findLatestByUidThreeMonthAgo(@Param("uid") Member uid, @Param("threeMonthsAgo") Date threeMonthsAgo) {
        return findLatestByUidAndDateBefore(uid, threeMonthsAgo);
    }

    default AssetSummary findLatestByUidFourMonthAgo(@Param("uid") Member uid, @Param("fourMonthsAgo") Date fourMonthsAgo) {
        return findLatestByUidAndDateBefore(uid, fourMonthsAgo);
    }

    default AssetSummary findLatestByUidFiveMonthAgo(@Param("uid") Member uid, @Param("fiveMonthsAgo") Date fiveMonthsAgo) {
        return findLatestByUidAndDateBefore(uid, fiveMonthsAgo);
    }

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO asset.asset_summary (
            uid, update_date, deposit, saving, subscription, withdrawal, cash, stock, coin, bond, total_amount
        )
        SELECT 
            b.uid, 
            NOW() AS update_date,
            COALESCE(SUM(CASE WHEN b.prod_category = '적금' THEN b.balance_amt ELSE 0 END), 0) AS deposit,
            COALESCE(SUM(CASE WHEN b.prod_category = '예금' THEN b.balance_amt ELSE 0 END), 0) AS saving,
            COALESCE(SUM(CASE WHEN b.prod_category = '청약' THEN b.balance_amt ELSE 0 END), 0) AS subscription,
            COALESCE(SUM(CASE WHEN b.prod_category = '입출금' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE((SELECT SUM(i.amount) FROM asset.income i WHERE i.uid = b.uid), 0) -
            COALESCE((SELECT SUM(o.amount) FROM outcome.outcome_user o WHERE o.uid = b.uid), 0) AS withdrawal,
            COALESCE(SUM(CASE WHEN b.prod_category = '현금' THEN b.balance_amt ELSE 0 END), 0) AS cash,
            COALESCE((SELECT SUM(s.hldg_qty * s.avg_buy_price) FROM asset.stock s WHERE s.uid = b.uid AND s.delete_date IS NULL), 0) AS stock,
            COALESCE((SELECT SUM(c.balance * c.avg_buy_price) FROM asset.coin c WHERE c.uid = b.uid AND c.delete_date IS NULL), 0) AS coin,
            COALESCE((SELECT SUM(bd.per_price * bd.cnt) FROM asset.bond bd WHERE bd.uid = b.uid AND bd.prod_category IS NOT NULL AND bd.delete_date IS NULL), 0) AS bond,
            COALESCE(SUM(CASE WHEN b.prod_category = '적금' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE(SUM(CASE WHEN b.prod_category = '예금' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE(SUM(CASE WHEN b.prod_category = '청약' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE(SUM(CASE WHEN b.prod_category = '입출금' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE(SUM(CASE WHEN b.prod_category = '현금' THEN b.balance_amt ELSE 0 END), 0) +
            COALESCE((SELECT SUM(s.hldg_qty * s.avg_buy_price) FROM asset.stock s WHERE s.uid = b.uid AND s.delete_date IS NULL), 0) +
            COALESCE((SELECT SUM(c.balance * c.avg_buy_price) FROM asset.coin c WHERE c.uid = b.uid AND c.delete_date IS NULL), 0) +
            COALESCE((SELECT SUM(bd.per_price * bd.cnt) FROM asset.bond bd WHERE bd.uid = b.uid AND bd.prod_category IS NOT NULL AND bd.delete_date IS NULL), 0) +
            COALESCE((SELECT SUM(i.amount) FROM asset.income i WHERE i.uid = b.uid), 0) -
            COALESCE((SELECT SUM(o.amount) FROM outcome.outcome_user o WHERE o.uid = b.uid), 0) AS total_amount
        FROM asset.bank b
        WHERE b.delete_date IS NULL AND b.uid = :uid
        GROUP BY b.uid
        ON DUPLICATE KEY UPDATE
            deposit = VALUES(deposit),
            saving = VALUES(saving),
            subscription = VALUES(subscription),
            withdrawal = VALUES(withdrawal),
            cash = VALUES(cash),
            stock = VALUES(stock),
            coin = VALUES(coin),
            bond = VALUES(bond),
            total_amount = VALUES(total_amount)
        """, nativeQuery = true)
    void insertOrUpdateAssetSummary(@Param("uid") int uid);

//    @Modifying
//    @Transactional
//    @Query(value = " select * FROM asset.asset_summary", nativeQuery = true)
//    void deleteDuplicateAssetSummary();


}
