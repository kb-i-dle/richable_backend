package com.idle.kb_i_dle_backend.finance.service;


import com.idle.kb_i_dle_backend.finance.dto.AssetDTO;
import com.idle.kb_i_dle_backend.finance.dto.CoinReturnDTO;
import com.idle.kb_i_dle_backend.finance.dto.FinancialAssetDTO;
import com.idle.kb_i_dle_backend.finance.dto.FinancialChangeDTO;
import com.idle.kb_i_dle_backend.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.finance.dto.MonthlySavingRateDTO;
import com.idle.kb_i_dle_backend.finance.dto.SpotAssetDTO;
import com.idle.kb_i_dle_backend.finance.dto.StockReturnDTO;
import com.idle.kb_i_dle_backend.finance.dto.TotalChangeDTO;
import com.idle.kb_i_dle_backend.finance.dto.BondReturnDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface FinanceService {

    // AS_1 금융 자산 합 조회
    FinancialSumDTO getFinancialAssetsSum(int uid);

    // AS_2 금융 + 현물 자산 합 조회
    FinancialSumDTO getTotalAssetsSum(int uid);

    // AS_3 금융 자산별 조회
    List<AssetDTO> getFinancialAsset(int uid);

    // AS_5 6개월간 금융 자산 변화 추이
    List<FinancialChangeDTO> getSixMonthFinancialChanges(int uid);


    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    List<TotalChangeDTO>getSixMonthTotalChanges(int uid);


    // AS_7 달별 저축률 추이
    List<MonthlySavingRateDTO> getMonthlySavingRateTrend(int uid);


    // AS_8 달별 주식 수익률
    List<StockReturnDTO>getStockReturnTrend(int uid);


    // AS_9 달별 가상화폐 수익률
    List<CoinReturnDTO> getCoinReturnTrend(int uid);

    // AS_10 달별 채권 수익률
    List<BondReturnDTO> getBondReturnTrend(int uid);


}
