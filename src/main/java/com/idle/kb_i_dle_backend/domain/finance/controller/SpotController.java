package com.idle.kb_i_dle_backend.domain.finance.controller;

import com.idle.kb_i_dle_backend.domain.finance.dto.SpotDTO;
import com.idle.kb_i_dle_backend.domain.finance.service.SpotService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;

import java.text.ParseException;
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
@RequestMapping("/finance")
@Slf4j
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;  // SpotServiceImpl 대신 SpotService 인터페이스로 주입
    private final MemberService memberService;


    // 카테고리에 따른 총 가격 반환
    @GetMapping("/spot/{category}/sum")
    public ResponseEntity<SuccessResponseDTO> getTotalPriceByCategory(@PathVariable("category") String category) {
        Integer uid = memberService.getCurrentUid();
        return ResponseEntity.ok(new SuccessResponseDTO(true, spotService.getTotalPriceByCategory(uid, category)));
    }


    // 현물 자산 총 가격 반환
    @GetMapping("/spot/sum")
    public ResponseEntity<SuccessResponseDTO> getTotalPriceByCategory() {
        Integer uid = memberService.getCurrentUid();
        return ResponseEntity.ok(new SuccessResponseDTO(true, spotService.getTotalPrice(uid)));
    }

    // 현물 자산 리스트 반환
    @GetMapping("/spot/all")
    public ResponseEntity<SuccessResponseDTO> getTotalSpotList() {
        Integer uid = memberService.getCurrentUid();
        return ResponseEntity.ok(new SuccessResponseDTO(true, spotService.getSpotList(uid)));
    }

    // 새로운 Spot 추가
    @PostMapping("/spot/add")
    public ResponseEntity<SuccessResponseDTO> addSpot(@RequestBody SpotDTO spotDTO) throws ParseException {
        Integer uid = memberService.getCurrentUid();
        return ResponseEntity.ok(new SuccessResponseDTO(true, spotService.addSpot(uid, spotDTO)));
    }

    // Spot 수정
    @PutMapping("/spot/update")
    public ResponseEntity<SuccessResponseDTO> updateSpot(@RequestBody SpotDTO spotDTO) {
        Integer uid = memberService.getCurrentUid();
        return ResponseEntity.ok(new SuccessResponseDTO(true, spotService.updateSpot(uid, spotDTO)));
    }

    // Spot 삭제
    @DeleteMapping("/spot/delete/{index}")
    public ResponseEntity<SuccessResponseDTO> deleteSpot(@PathVariable("index") Integer index) {
        Integer uid = memberService.getCurrentUid();
        Map<String, Object> indexData = new HashMap<>();
        indexData.put("index", spotService.deleteSpot(uid, index).getIndex());
        return ResponseEntity.ok(new SuccessResponseDTO(true, indexData));
    }
}
