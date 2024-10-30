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

        if (uid == null) {
            throw new IllegalArgumentException("UID is missing==============================================");
        }

        FinancialSumDTO totalPrice = financeService.getFinancialAssetsSum(uid);
        SuccessResponseDTO Response = new SuccessResponseDTO(true, totalPrice);

        return ResponseEntity.ok(Response);
    }


    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/fin")
    public ResponseEntity<?> getTotalAsset() {
        Integer uid = memberService.getCurrentUid();

        List<AssetDTO> totalPrice = financeService.getFinancialAsset(uid);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, totalPrice);

        return ResponseEntity.ok(successResponse);
    }

    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/total/sum")
    public ResponseEntity<?> getTotalAssetsSum() {
        Integer uid = memberService.getCurrentUid();

        FinancialSumDTO totalPrice = financeService.getTotalAssetsSum(uid);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, totalPrice);

        return ResponseEntity.ok(successResponse);
    }


    // AS_5 6개월간 금융 자산 변화 추이
    @GetMapping("/changed/fin")
    public ResponseEntity<?> getSixMonthFinancialChanges() {
        Integer uid = memberService.getCurrentUid();

        List<FinancialChangeDTO> result = financeService.getSixMonthFinancialChanges(uid);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, result);

        return ResponseEntity.ok(successResponse);
    }

    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    @GetMapping("/changed/spot")
    public ResponseEntity<?> getSixMonthTotalChanges() {
        Integer uid = memberService.getCurrentUid();

        List<TotalChangeDTO> result = financeService.getSixMonthTotalChanges(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, result);

        return ResponseEntity.ok(successResponse);
    }

    // AS_7 달별 저축률 추이
    @GetMapping("/return/income")
    public ResponseEntity<?> getMonthlySavingRateTrend() {
        Integer uid = memberService.getCurrentUid();
        List<MonthlyBalanceDTO> totalPrice = financeService.getMonthlyIncomeOutcomeBalance(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, totalPrice);

        return ResponseEntity.ok(successResponse);

    }

    // AS_8 달별 주식 수익률
    @GetMapping("/return/stock")
    public ResponseEntity<?> getStockReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<StockReturnDTO> stockReturn = financeService.getStockReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, stockReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_9 달별 가상화폐 수익률
    @GetMapping("/return/coin")
    public ResponseEntity<?> getCoinReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<CoinReturnDTO> coinReturn = financeService.getCoinReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, coinReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_10 달별 채권 수익률
    @GetMapping("/return/bond")
    public ResponseEntity<?> getBondReturnTrend() {
        Integer uid = memberService.getCurrentUid();
        List<BondReturnDTO> bondReturn = financeService.getBondReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, bondReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_11
    @GetMapping("/peer")
    public ResponseEntity<SuccessResponseDTO> compareAssetsWithAgeGroup() {
        Integer uid = memberService.getCurrentUid();
        Map<String, Object> response = financeService.compareAssetsWithAgeGroup(uid);
        SuccessResponseDTO Response = new SuccessResponseDTO(true, response);

        return ResponseEntity.ok(Response);
    }

    @GetMapping("/peer/finance")
    public ResponseEntity<SuccessResponseDTO> compareAssetsByCategoryWithAgeGroup() {
        Integer uid = memberService.getCurrentUid();
        List<Map<String, Object>> response = financeService.compareAssetsByCategoryWithAgeGroup(uid);
        SuccessResponseDTO Response = new SuccessResponseDTO(true, response);

        return ResponseEntity.ok(Response);
    }

    @GetMapping("/product/bond")
    public ResponseEntity<SuccessResponseDTO> getBondProductList() {
        List<BondProduct> bondProducts = financeService.findBondProductsWithNonNullPrices();
        SuccessResponseDTO response = new SuccessResponseDTO(true, bondProducts);
        return ResponseEntity.ok(response);
    }

    //    @GetMapping("/product/stock")
//    public ResponseEntity<SuccessResponseDTO> getStockProductList() {
//        // 시작 시간 측정
//        long startTime = System.currentTimeMillis();
//        List<StockProduct> stockProducts = financeService.findStockProducts();
//        // 종료 시간 측정
//        long endTime = System.currentTimeMillis();
//        // 소요 시간 계산
//        long duration = endTime - startTime;
//        System.out.println("findStockProducts() 수행 시간: " + duration + " ms");
//        SuccessResponseDTO response = new SuccessResponseDTO(true, stockProducts);
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("/product/stock")
    public ResponseEntity<SuccessResponseDTO> getStockProductList(@RequestParam(defaultValue = "200") int limit) {
        List<StockProduct> stockProducts = financeService.findStockProducts(limit);
        SuccessResponseDTO response = new SuccessResponseDTO(true, stockProducts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/coin")
    public ResponseEntity<SuccessResponseDTO> getCoinProductList() {
        List<CoinProduct> coinProducts = financeService.findCoinProducts();
        SuccessResponseDTO response = new SuccessResponseDTO(true, coinProducts);
        return ResponseEntity.ok(response);
    }

}
