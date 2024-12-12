package com.idle.kb_i_dle_backend.domain.income.controller;

import com.idle.kb_i_dle_backend.domain.income.dto.IncomeDTO;
import com.idle.kb_i_dle_backend.domain.income.service.IncomeService;
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
@RequestMapping("/income")
@Slf4j
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;
    private final MemberService memberService;

    // 소득 항목 리스트 반환
    @GetMapping("/all")
    public ResponseEntity<?> getTotalIncomeList() {
            Integer uid = memberService.getCurrentUid();
            SuccessResponseDTO response = new SuccessResponseDTO(true, incomeService.getIncomeList(uid));
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 소득 항목 리스트 반환
    @GetMapping("/detail/{index}")
    public ResponseEntity<?> getIncomeDetail(@PathVariable("index") Integer index) {
            Integer uid = memberService.getCurrentUid();
            SuccessResponseDTO response = new SuccessResponseDTO(true, incomeService.getIncomeByIndex(uid, index));
            return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 새로운 소득 추가
    @PostMapping("/add")
    public ResponseEntity<?> addIncome(@RequestBody IncomeDTO incomeDTO) {
            Integer uid = memberService.getCurrentUid();
            SuccessResponseDTO response = new SuccessResponseDTO(true, incomeService.addIncome(uid, incomeDTO));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 소득 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateIncome(@RequestBody IncomeDTO incomeDTO) {
            Integer uid = memberService.getCurrentUid();
            SuccessResponseDTO response = new SuccessResponseDTO(true, incomeService.updateIncome(uid, incomeDTO));
            return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 소득 삭제
    @DeleteMapping("/delete/{index}")
    public ResponseEntity<?> deleteIncome(@PathVariable("index") Integer index) {
            Integer uid = memberService.getCurrentUid();
            Map<String, Object> indexData = new HashMap<>();
            indexData.put("index", incomeService.deleteIncomeByUidAndIndex(uid, index));
            SuccessResponseDTO response = new SuccessResponseDTO(true, indexData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


