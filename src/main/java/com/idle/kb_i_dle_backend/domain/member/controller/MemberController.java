package com.idle.kb_i_dle_backend.domain.member.controller;

import com.idle.kb_i_dle_backend.domain.member.dto.LoginDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberJoinDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.VerificationResult;
import com.idle.kb_i_dle_backend.domain.member.dto.VerificationType;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;

import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@PropertySource({"classpath:/application.properties"})
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> loginResult = memberService.login(loginDTO);
        return ResponseEntity.ok(new SuccessResponseDTO(true, loginResult));
    }

    @GetMapping("/naverlogin")
    public ResponseEntity<SuccessResponseDTO> naverlogin(HttpServletRequest request) {
        try {
            Map<String, Object> naverLoginResult = memberService.initiateNaverLogin(request);
            log.error("result check for social login : " + naverLoginResult);
            return ResponseEntity.ok(new SuccessResponseDTO(true, naverLoginResult));
        } catch (Exception e) {
            log.error("Failed to initiate Naver login", e);
            String result = "네이버 로그인 초기화 중 오류가 발생했습니다.";
            return (ResponseEntity<SuccessResponseDTO>) ResponseEntity.internalServerError();
        }
    }

    @GetMapping("/navercallback")
    public ResponseEntity<?> naverCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            Map<String, Object> callbackResult = memberService.processNaverCallback(code, state);
            String token = (String) callbackResult.get("token");
            String frontendUrl = "https://www.richable.site";  // 프론트엔드 URL
            String redirectUrl = frontendUrl + "/auth/naver/callback?token=" + token;

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("Failed to process Naver callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("네이버 로그인 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> signup(@RequestBody MemberJoinDTO signupDTO) {
        String result = memberService.registerMember(signupDTO);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    @GetMapping("/checkDupl/{id}")
    public ResponseEntity<?> checkDuplicateUsername(@PathVariable String id) {
        boolean result = memberService.checkDupl(id);
        if (result == true) {
            SuccessResponseDTO successResponse = new SuccessResponseDTO(true, result);
            return ResponseEntity.ok(successResponse);
        } else {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("중복된 ID");
            return ResponseEntity.ok(errorResponseDTO);
        }
    }

    @PostMapping("/agree/{id}")
    public ResponseEntity<?> updateUserAgreement(@PathVariable String id, @RequestBody Map<String, Boolean> agreementData) {
        boolean result = memberService.updateUserAgreement(id, agreementData);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    @PostMapping("/find/id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> payload) {
        Map<String, String> result = memberService.findIdByEmail(payload.get("email"));
        boolean isSuccess = !result.containsKey("error");
        return ResponseEntity.ok(new SuccessResponseDTO(isSuccess, result));
    }

    @PostMapping("/find/id/auth")
    public ResponseEntity<SuccessResponseDTO> verifyIdCode(@RequestBody Map<String, String> payload) {
        VerificationResult result = memberService.verifyCode(payload.get("email"), payload.get("code"), VerificationType.ID);
        return ResponseEntity.ok(new SuccessResponseDTO(result.isVerified(), result));
    }

    @PostMapping("/find/pw")
    public ResponseEntity<?> findPw(@RequestBody Map<String, String> payload) {
        String result = memberService.findPwByEmail(payload.get("email"));
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    @PostMapping("/find/pw/auth")
    public ResponseEntity<SuccessResponseDTO> verifyPasswordCode(@RequestBody Map<String, String> payload) {
        VerificationResult result = memberService.verifyCode(payload.get("email"), payload.get("code"), VerificationType.PASSWORD);
        return ResponseEntity.ok(new SuccessResponseDTO(result.isVerified(), result));
    }

    @PostMapping("/set/pw")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        boolean result = memberService.resetPassword(payload.get("id"), payload.get("newPassword"));
        return ResponseEntity.ok(new SuccessResponseDTO(result, "비밀번호 재설정에 성공하였습니다."));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfoByNickname(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Map<String, Object> memberInfo = memberService.getMemberInfoByToken(token);
        return ResponseEntity.ok(new SuccessResponseDTO(true, memberInfo));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMemberInfo(@RequestBody Map<String, Object> updatedMemberInfo, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Map<String, Object> result = memberService.updateMemberInfo(updatedMemberInfo, token);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }

    @DeleteMapping("/delete/{nickname}")
    public ResponseEntity<?> deleteMember(@PathVariable String nickname) {
        boolean result = memberService.deleteMemberById(nickname);
        return ResponseEntity.ok(new SuccessResponseDTO(true, result));
    }
}