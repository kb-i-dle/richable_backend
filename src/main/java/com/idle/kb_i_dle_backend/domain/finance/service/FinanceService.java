package com.idle.kb_i_dle_backend.domain.finance.service;


import com.idle.kb_i_dle_backend.domain.finance.dto.AssetDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.MonthlyBalanceDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.TotalChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockProduct;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FinanceService {

    // AS_1 금융 자산 합 조회
    FinancialSumDTO getFinancialAssetsSum(int uid);

    // AS_2 금융 + 현물 자산 합 조회
    FinancialSumDTO getTotalAssetsSum(int uid);

    FinancialSumDTO getAssetSummeryByDateBefore(int uid, Date date);

    // AS_3 금융 자산별 조회
    List<AssetDTO> getFinancialAsset(int uid);

    // AS_5 6개월간 금융 자산 변화 추이
    List<FinancialChangeDTO> getSixMonthFinancialChanges(int uid);

    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    List<TotalChangeDTO> getSixMonthTotalChanges(int uid);

    // AS_8 달별 주식 수익률
    List<StockReturnDTO> getStockReturnTrend(int uid);

    // AS_9 달별 가상화폐 수익률
    List<CoinReturnDTO> getCoinReturnTrend(int uid);

    // AS_10 달별 채권 수익률
    List<BondReturnDTO> getBondReturnTrend(int uid);

    List<MonthlyBalanceDTO> getMonthlyIncomeOutcomeBalance(int uid);

    Map<String, Object> compareAssetsWithAgeGroup(int uid);

    List<Map<String, Object>> compareAssetsByCategoryWithAgeGroup(int uid);

    List<BondProduct> findBondProductsWithNonNullPrices();

    List<StockProduct> findStockProducts();

    List<CoinProduct> findCoinProducts();

    void scheduleAssetSummaryUpdate();

    List<StockProduct> findStockProducts(int limit);

}
