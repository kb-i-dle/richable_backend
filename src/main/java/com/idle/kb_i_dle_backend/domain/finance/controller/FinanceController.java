package com.idle.kb_i_dle_backend.domain.finance.controller;

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
import com.idle.kb_i_dle_backend.domain.finance.service.FinanceService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@Slf4j
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final MemberService memberService;


    // AS_1 금융 자산 합 조회
    // // 토큰 값 불러오는 방식 구현 완료
    @GetMapping("/fin/sum")
    public ResponseEntity<SuccessResponseDTO> getFinancialAssetsSum() {
        Integer uid = memberService.getCurrentUid();
        FinancialSumDTO totalPrice = financeService.getFinancialAssetsSum(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, totalPrice));
    }


    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/fin")
    public ResponseEntity<SuccessResponseDTO> getTotalAsset() {
        Integer uid = memberService.getCurrentUid();
        List<AssetDTO> totalPrice = financeService.getFinancialAsset(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, totalPrice));
    }

    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/total/sum")
    public ResponseEntity<SuccessResponseDTO> getTotalAssetsSum() {
        Integer uid = memberService.getCurrentUid();
        FinancialSumDTO totalPrice = financeService.getTotalAssetsSum(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, totalPrice));
    }


    // AS_5 6개월간 금융 자산 변화 추이
    @GetMapping("/changed/fin")
    public ResponseEntity<SuccessResponseDTO> getSixMonthFinancialChanges() {
        Integer uid = memberService.getCurrentUid();
        List<FinancialChangeDTO> result = financeService.getSixMonthFinancialChanges(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    @GetMapping("/changed/spot")
    public ResponseEntity<SuccessResponseDTO> getSixMonthTotalChanges() {
        Integer uid = memberService.getCurrentUid();
        List<TotalChangeDTO> result = financeService.getSixMonthTotalChanges(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    // AS_7 달별 저축률 추이
    @GetMapping("/return/income")
    public ResponseEntity<SuccessResponseDTO> getMonthlySavingRateTrend() {
        Integer uid = memberService.getCurrentUid();
        List<MonthlyBalanceDTO> totalPrice = financeService.getMonthlyIncomeOutcomeBalance(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, totalPrice));
    }

    // AS_8 달별 주식 수익률
    @GetMapping("/return/stock")
    public ResponseEntity<SuccessResponseDTO> getStockReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<StockReturnDTO> stockReturn = financeService.getStockReturnTrend(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, stockReturn));
    }

    // AS_9 달별 가상화폐 수익률
    @GetMapping("/return/coin")
    public ResponseEntity<SuccessResponseDTO> getCoinReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<CoinReturnDTO> coinReturn = financeService.getCoinReturnTrend(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, coinReturn));
    }

    // AS_10 달별 채권 수익률
    @GetMapping("/return/bond")
    public ResponseEntity<SuccessResponseDTO> getBondReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<BondReturnDTO> bondReturn = financeService.getBondReturnTrend(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, bondReturn));
    }

    // AS_11
    @GetMapping("/peer")
    public ResponseEntity<SuccessResponseDTO> compareAssetsWithAgeGroup() {
        Integer uid = memberService.getCurrentUid();
        Map<String, Object> response = financeService.compareAssetsWithAgeGroup(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, response));
    }

    @GetMapping("/peer/finance")
    public ResponseEntity<SuccessResponseDTO> compareAssetsByCategoryWithAgeGroup() {
        Integer uid = memberService.getCurrentUid();
        List<Map<String, Object>> response = financeService.compareAssetsByCategoryWithAgeGroup(uid);
        return ResponseEntity.ok(new SuccessResponseDTO(true, response));
    }

    @GetMapping("/product/bond")
    public ResponseEntity<SuccessResponseDTO> getBondProductList() {
        List<BondProduct> bondProducts = financeService.findBondProductsWithNonNullPrices();
        return ResponseEntity.ok(new SuccessResponseDTO(true, bondProducts));
    }

    @GetMapping("/product/stock")
    public ResponseEntity<SuccessResponseDTO> getStockProductList(@RequestParam(defaultValue = "200") int limit) {
        List<StockProduct> stockProducts = financeService.findStockProducts(limit);
        return ResponseEntity.ok(new SuccessResponseDTO(true, stockProducts));
    }

    @GetMapping("/product/coin")
    public ResponseEntity<SuccessResponseDTO> getCoinProductList() {
        List<CoinProduct> coinProducts = financeService.findCoinProducts();
        return ResponseEntity.ok(new SuccessResponseDTO(true, coinProducts));
    }

}
