package com.idle.kb_i_dle_backend.domain.finance.controller;

import com.idle.kb_i_dle_backend.domain.finance.dto.AssetDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.MonthlyBalanceDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.TotalChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.service.FinanceService;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@Slf4j
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;


    // AS_1 금융 자산 합 조회
    // // 토큰 값 불러오는 방식 구현 완료
    @GetMapping("/fin/sum")
    public ResponseEntity<SuccessResponseDTO> getFinancialAssetsSum(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");

        if (uid == null) {
            throw new IllegalArgumentException("UID is missing==============================================");
        }

        FinancialSumDTO totalPrice = financeService.getFinancialAssetsSum(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO Response = new SuccessResponseDTO(true, responseData);

        return ResponseEntity.ok(Response);
    }


    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/fin")
    public ResponseEntity<?> getTotalAsset(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");

        List<AssetDTO> totalPrice = financeService.getFinancialAsset(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/total/sum")
    public ResponseEntity<?> getTotalAssetsSum(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");

        FinancialSumDTO totalPrice = financeService.getTotalAssetsSum(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, responseData);

        return ResponseEntity.ok(successResponse);
    }


    // AS_5 6개월간 금융 자산 변화 추이
    @GetMapping("/changed/fin")
    public ResponseEntity<?> getSixMonthFinancialChanges(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");

        List<FinancialChangeDTO> result = financeService.getSixMonthFinancialChanges(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", result);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    @GetMapping("/changed/spot")
    public ResponseEntity<?> getSixMonthTotalChanges(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");

        List<TotalChangeDTO> result = financeService.getSixMonthTotalChanges(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, result);

        return ResponseEntity.ok(successResponse);
    }

    // AS_7 달별 저축률 추이
    @GetMapping("/return/income")
    public ResponseEntity<?> getMonthlySavingRateTrend(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        List<MonthlyBalanceDTO> totalPrice = financeService.getMonthlyIncomeOutcomeBalance(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, totalPrice);

        return ResponseEntity.ok(successResponse);

    }

    // AS_8 달별 주식 수익률
    @GetMapping("/return/stock")
    public ResponseEntity<?> getStockReturnTrend(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        List<StockReturnDTO> stockReturn = financeService.getStockReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, stockReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_9 달별 가상화폐 수익률
    @GetMapping("/return/coin")
    public ResponseEntity<?> getCoinReturnTrend(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        List<CoinReturnDTO> coinReturn = financeService.getCoinReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, coinReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_10 달별 채권 수익률
    @GetMapping("/return/bond")
    public ResponseEntity<?> getBondReturnTrend(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        List<BondReturnDTO> bondReturn = financeService.getBondReturnTrend(uid);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(true, bondReturn);

        return ResponseEntity.ok(successResponse);
    }

    // AS_11
    @GetMapping("/peer")
    public ResponseEntity<SuccessResponseDTO> compareAssetsWithAgeGroup(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        Map<String, Object> response = financeService.compareAssetsWithAgeGroup(uid);
        SuccessResponseDTO Response = new SuccessResponseDTO(true, response);

        return ResponseEntity.ok(Response);
    }

    @GetMapping("/peer/finance")
    public ResponseEntity<SuccessResponseDTO> compareAssetsByCategoryWithAgeGroup(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        List<Map<String, Object>> response = financeService.compareAssetsByCategoryWithAgeGroup(uid);
        SuccessResponseDTO Response = new SuccessResponseDTO(true, response);

        return ResponseEntity.ok(Response);

    }


}
