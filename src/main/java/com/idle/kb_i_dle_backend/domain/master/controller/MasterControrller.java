package com.idle.kb_i_dle_backend.domain.master.controller;

import com.idle.kb_i_dle_backend.domain.master.service.MasterService;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/master")
@RequiredArgsConstructor
@Slf4j
public class MasterControrller {

    private final MasterService masterService;

    // 주식 가격 업데이트
    @GetMapping("/update/stock")
    public ResponseEntity<?> updateStockPrice(HttpServletRequest request) {
        String result = masterService.updateStockPrices();
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    // 주식 가격 업데이트
    @GetMapping("/update/stock/before")
    public ResponseEntity<?> updateStockPricesBefore(HttpServletRequest request) {
        String result = masterService.updateStockPricesBefore();
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    // 코인 가격 업데이트
    @GetMapping("/update/coin")
    public ResponseEntity<?> updateCoinPrice(HttpServletRequest request) {
        String result = masterService.updateCoinPrices();
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }
}
