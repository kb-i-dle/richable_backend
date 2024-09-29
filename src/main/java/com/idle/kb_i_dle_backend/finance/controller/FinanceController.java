package com.idle.kb_i_dle_backend.finance.controller;

import com.idle.kb_i_dle_backend.common.dto.SuccessResponseDTO;
import com.idle.kb_i_dle_backend.finance.dto.AssetDTO;
import com.idle.kb_i_dle_backend.finance.dto.BondReturnDTO;
import com.idle.kb_i_dle_backend.finance.dto.CoinReturnDTO;
import com.idle.kb_i_dle_backend.finance.dto.FinancialChangeDTO;
import com.idle.kb_i_dle_backend.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.finance.dto.MonthlySavingRateDTO;
import com.idle.kb_i_dle_backend.finance.dto.StockReturnDTO;
import com.idle.kb_i_dle_backend.finance.dto.TotalChangeDTO;
import com.idle.kb_i_dle_backend.finance.service.FinanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    // AS_1 금융 자산 합 조회
    @GetMapping("/fin/sum")
    public ResponseEntity<SuccessResponseDTO> getFinancialAssetsSum(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        if (uid == null) {
            throw new IllegalArgumentException("UID is missing==============================================");
        }

        FinancialSumDTO totalPrice = financeService.getFinancialAssetsSum(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);


        return ResponseEntity.ok(successResponse);
    }


    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/fin")
    public ResponseEntity<?> getTotalAsset(HttpServletRequest request) {

        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");

        List<AssetDTO> totalPrice = financeService.getFinancialAsset(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_2 금융 +현물 자산 합 조회
    @GetMapping("/total/sum")
    public ResponseEntity<?> getTotalAssetsSum(HttpServletRequest request) {

        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");

        FinancialSumDTO totalPrice = financeService.getTotalAssetsSum(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }


    // AS_5 6개월간 금융 자산 변화 추이
    @GetMapping("/changed/fin")
    public ResponseEntity<?> getSixMonthFinancialChanges(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");

        List<FinancialChangeDTO> result = financeService.getSixMonthFinancialChanges(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", result);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_6 6개월간 금융 자산 + 현물량 변화 추이
    @GetMapping("/changed/spot")
    public ResponseEntity<?> getSixMonthTotalChanges(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");

        List<TotalChangeDTO> result = financeService.getSixMonthTotalChanges(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", result);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_7 달별 저축률 추이
    @GetMapping("/return/income")
    public ResponseEntity<?> getMonthlySavingRateTrend(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        List<MonthlySavingRateDTO> totalPrice = financeService.getMonthlySavingRateTrend(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", totalPrice);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);

    }

    // AS_8 달별 주식 수익률
    @GetMapping("/return/stock")
    public ResponseEntity<?> getStockReturnTrend(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        List<StockReturnDTO> stockReturn = financeService.getStockReturnTrend(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", stockReturn);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_9 달별 가상화폐 수익률
    @GetMapping("/return/coin")
    public ResponseEntity<?> getCoinReturnTrend(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        List<CoinReturnDTO> coinReturn = financeService.getCoinReturnTrend(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", coinReturn);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // AS_10 달별 채권 수익률
    @GetMapping("/return/bond")
    public ResponseEntity<?> getBondReturnTrend(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        List<BondReturnDTO> bondReturn = financeService.getBondReturnTrend(uid);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", bondReturn);

        SuccessResponseDTO successResponse = new SuccessResponseDTO("true", responseData);

        return ResponseEntity.ok(successResponse);
    }
}
