package com.idle.kb_i_dle_backend.domain.member.controller;


import com.idle.kb_i_dle_backend.domain.member.dto.MemberBadgeDTO;
import com.idle.kb_i_dle_backend.domain.member.service.UserBadgeService;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class UserBadgeController {

    private final UserBadgeService userBadgeService;


    // 목표 달성 뱃지 조회
    @GetMapping("/badge/having/{nickname}")
    public ResponseEntity<SuccessResponseDTO> getAchievedBadges(@PathVariable String nickname) {
        List<MemberBadgeDTO> badges = userBadgeService.getUserBadges(nickname, true);

        // response 감싸기
        SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(true, badges);

        return new ResponseEntity<>(successResponseDTO, HttpStatus.OK);
    }

    // 목표 미달성 뱃지 조회
    @GetMapping("/badge/nothaving/{nickname}")
    public ResponseEntity<SuccessResponseDTO> getNotAchievedBadges(@PathVariable String nickname) {
        List<MemberBadgeDTO> badges = userBadgeService.getUserBadges(nickname, false);

        return new ResponseEntity<>(new SuccessResponseDTO(true, badges), HttpStatus.OK);

    }

    // 전체 뱃지 조회
    @GetMapping("/badge/all/{nickname}")
    public ResponseEntity<SuccessResponseDTO> getAllBadges(@PathVariable String nickname) {
        List<MemberBadgeDTO> badges = userBadgeService.getAllBadges(nickname);

        return ResponseEntity.ok(new SuccessResponseDTO(true, badges));
    }
}
