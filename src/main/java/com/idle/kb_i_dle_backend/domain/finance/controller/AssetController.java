package com.idle.kb_i_dle_backend.domain.finance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.kb_i_dle_backend.domain.finance.dto.BankDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockDTO;
import com.idle.kb_i_dle_backend.domain.finance.service.BankService;
import com.idle.kb_i_dle_backend.domain.finance.service.BondService;
import com.idle.kb_i_dle_backend.domain.finance.service.CoinService;
import com.idle.kb_i_dle_backend.domain.finance.service.StockService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asset")
@Slf4j
@RequiredArgsConstructor
public class AssetController {
    private final BankService bankService;
    private final BondService bondService;
    private final CoinService coinService;
    private final StockService stockService;
    private final MemberService memberService;

    // asset 리스트 반환
    @GetMapping("/{category}/all")
    public ResponseEntity<SuccessResponseDTO> getAssetList(@PathVariable("category") String category) {
        Integer uid = memberService.getCurrentUid();
        return switch (category) {
            case "bank" -> ResponseEntity.ok(new SuccessResponseDTO(true, bankService.getBankList(uid)));
            case "bond" -> ResponseEntity.ok(new SuccessResponseDTO(true, bondService.getBondList(uid)));
            case "coin" -> ResponseEntity.ok(new SuccessResponseDTO(true, coinService.getCoinList(uid)));
            default -> ResponseEntity.ok(new SuccessResponseDTO(true, stockService.getStockList(uid)));
        };
    }

    // Asset 추가
    @PostMapping("/{category}/add")
    public ResponseEntity<?> addAsset(@PathVariable("category") String category, @RequestBody String reqBody) {
        Integer uid = memberService.getCurrentUid();
        switch (category) {
            case "bank" -> {
                BankDTO bankData = convertToDTO(reqBody, BankDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, bankService.addBank(uid, bankData)));
            }
            case "bond" -> {
                BondDTO bondData = convertToDTO(reqBody, BondDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, bondService.addBond(uid, bondData)));
            }
            case "coin" -> {
                CoinDTO coinData = convertToDTO(reqBody, CoinDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, coinService.addCoin(uid, coinData)));
            }
            default -> {
                StockDTO stockData = convertToDTO(reqBody, StockDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, stockService.addStock(uid, stockData)));
            }
        }
    }


    // Asset 수정
    @PutMapping("/{category}/update")
    public ResponseEntity<?> updateAsset(@PathVariable("category") String category, @RequestBody String reqBody) {
        Integer uid = memberService.getCurrentUid();
        switch (category) {
            case "bank" -> {
                BankDTO bankData = convertToDTO(reqBody, BankDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, bankService.updateBank(uid, bankData)));
            }
            case "bond" -> {
                BondDTO bondData = convertToDTO(reqBody, BondDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, bondService.updateBond(uid, bondData)));
            }
            case "coin" -> {
                CoinDTO coinData = convertToDTO(reqBody, CoinDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, coinService.updateCoin(uid, coinData)));
            }
            default -> {
                StockDTO stockData = convertToDTO(reqBody, StockDTO.class);
                return ResponseEntity.ok(new SuccessResponseDTO(true, stockService.updateStock(uid, stockData)));
            }
        }
    }

    // Asset 삭제
    @DeleteMapping("/{category}/delete/{index}")
    public ResponseEntity<SuccessResponseDTO> updateAsset(@PathVariable("category") String category, @PathVariable("index") Integer index) {
        Integer uid = memberService.getCurrentUid();
        switch (category) {
            case "bank" -> {
                Map<String, Object> indexData = new HashMap<>();
                indexData.put("index", bankService.deleteBank(uid, index).getIndex());
                return ResponseEntity.ok(new SuccessResponseDTO(true, indexData));
            }
            case "bond" -> {
                Map<String, Object> indexData = new HashMap<>();
                indexData.put("index", bondService.deleteBond(uid, index).getIndex());
                return ResponseEntity.ok(new SuccessResponseDTO(true, indexData));
            }
            case "coin" -> {
                Map<String, Object> indexData = new HashMap<>();
                indexData.put("index", coinService.deleteCoin(uid, index).getIndex());
                return ResponseEntity.ok(new SuccessResponseDTO(true, indexData));
            }
            default -> {
                Map<String, Object> indexData = new HashMap<>();
                indexData.put("index", stockService.deleteStock(uid, index).getIndex());
                return ResponseEntity.ok(new SuccessResponseDTO(true, indexData));
            }
        }
    }

    // 입출금 계좌 조회
    @GetMapping("/account/list")
    public ResponseEntity<SuccessResponseDTO> listOfAccount() {
        Integer uid = memberService.getCurrentUid();
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("account", bankService.getAccount(uid));
        return ResponseEntity.ok(new SuccessResponseDTO(true, accountData));
    }

    private <T> T convertToDTO(String reqBody, Class<T> dtoClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(reqBody, dtoClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

}
