package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.member.dto.*;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.exception.MemberException;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberApiService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {



    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtProcessor jwtProcessor;
    private final MemberInfoServiceImpl memberInfoService;
    private final MemberApiService memberApiService;
    private final RestTemplate restTemplate;
    private final HttpServletRequest request;
    private final AssetSummaryRepository assetSummaryRepository;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.redirect.uri}")
    private String redirectUri;

    private Map<String, String> verificationCodes = new HashMap<>();

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getId(), loginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = customUserDetails.getMember();
            MemberInfoDTO userInfo = new MemberInfoDTO(member.getUid(), member.getId(),
                    member.getEmail(), member.getNickname(), member.getAuth());

            String jwtToken = jwtProcessor.generateToken(userInfo.getId(), userInfo.getUid(), userInfo.getNickname(),
                    userInfo.getEmail());

            // 자산 리포트 업데이트
            assetSummaryRepository.insertOrUpdateAssetSummary(userInfo.getUid());

            Map<String, Object> result = new HashMap<>();
            result.put("token", jwtToken);
            result.put("userInfo", userInfo);
            return result;
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            throw new MemberException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    public String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @Override
    public Map<String, Object> initiateNaverLogin(HttpServletRequest request) {
        try {
            String state = UUID.randomUUID().toString();
            request.getSession().setAttribute("naverState", state);

            String authorizationUrl = "https://nid.naver.com/oauth2.0/authorize"
                    + "?response_type=code"
                    + "&client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&state=" + state;
            log.info("initiateNaverLogin");

            return Map.of("redirectUrl", authorizationUrl);
        } catch (Exception e) {
            log.error("Failed to initiate Naver login", e);
            return Map.of("error", "네이버 로그인 초기화 중 오류가 발생했습니다.");
        }
    }

    @Override
    public Map<String, Object> processNaverCallback(String code, String state) {
        // 1. 액세스 토큰 얻기
        log.info("processNaverCallback");
        String accessToken = getNaverAccessToken(code, state);
        log.info("getNaverAccessToken");
        // 2. 사용자 정보 얻기
        Map<String, Object> userInfo = getNaverUserInfo(accessToken);
        log.info("getNaverUserInfo");
        // 3. 회원 정보 확인 및 처리
        Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
        String naverEmail = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String birth_year = (String) response.get("birthyear");
        char gender = response.get("gender").toString().charAt(0);
        log.info("response check step");
        Member member = memberRepository.findByEmail(naverEmail);

        if (member == null) {
            // 새 회원 등록 필요
            member = createNaverMember(naverEmail, nickname,gender,birth_year);
            CustomUserDetails userDetails = new CustomUserDetails(member);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtProcessor.generateToken(member.getId(), member.getUid(), member.getNickname(), member.getEmail());

            return Map.of(
                    "isNewUser", false,
                    "token", jwtToken,
                    "userInfo", new MemberInfoDTO(member.getUid(), member.getId(), member.getEmail(), member.getNickname(), member.getAuth())
            );
        } else {
            // 기존 회원 로그인 처리
            CustomUserDetails userDetails = new CustomUserDetails(member);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtProcessor.generateToken(member.getId(), member.getUid(), member.getNickname(), member.getEmail());

            return Map.of(
                    "isNewUser", false,
                    "token", jwtToken,
                    "userInfo", new MemberInfoDTO(member.getUid(), member.getId(), member.getEmail(), member.getNickname(), member.getAuth())
            );
        }
    }

    private String getNaverAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("access_token");
        } else {
            throw new MemberException(ErrorCode.NAVER_LOGIN_FAILED, "Failed to obtain access token");
        }
    }

    private Map getNaverUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity entity = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me", HttpMethod.GET, entity, Map.class);
        log.error("response: "+ response.getBody()+ " / "+response);

        return (Map) response.getBody();
    }

    private Member createNaverMember(String email, String nickname,char gender, String birth_year) {
        String id = email.split("@")[0];
        String password = UUID.randomUUID().toString();

        MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .id(id)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .email(email)
                .birth_year(Integer.valueOf(birth_year))
                .auth("ROLE_MEMBER")
                .agreementInfo(true)
                .agreementFinance(true)
                .build();

        Member member = Member.from(memberJoinDTO);
        member.setPassword(passwordEncoder.encode(password));

        return memberRepository.save(member);
    }

    @Override
    public String registerMember(MemberJoinDTO signupDTO) {
        try {
            MemberJoin(signupDTO);
            return "User registered successfully";
        } catch (IllegalStateException e) {
            throw new MemberException(ErrorCode.USER_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new MemberException(ErrorCode.REGISTRATION_FAILED);
        }
    }

    @Override
    public Map<String, Object> checkDupl(String id) {
        boolean exists = memberRepository.existsById(id);
        Map<String, Object> result = new HashMap<>();

        if (!exists) {
            result.put("available", true);
            result.put("message", "사용 가능한 ID입니다");
        } else {
            result.put("available", false);
            result.put("message", "중복된 ID입니다");
        }

        return result;
    }

    @Override
    public Member findMemberByUid(int id) {
        try {
            Member member = memberRepository.findByUid(id);
            if (member == null) {
                throw new MemberException(ErrorCode.INVALID_MEMEBER);
            }
            return member;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.INVALID_MEMEBER, e.getMessage());
        }
    }

    @Override
    public Member findMemberByNickname(String nickname) {
        try {
            Member member = memberRepository.findByNickname(nickname);
            if (member == null) {
                throw new MemberException(ErrorCode.INVALID_MEMEBER);
            }
            return member;
        } catch (Exception e) {
            throw new MemberException(ErrorCode.INVALID_MEMEBER, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void MemberJoin(MemberJoinDTO memberjoindto) {
        try {
            log.debug("Starting MemberJoin process for ID: {}", memberjoindto.getId());

            if (memberjoindto.isAgreementInfo()) {
                memberjoindto.setAgreementInfo(true);
            } else {
                memberjoindto.setAgreementInfo(false);
            }
            if (memberjoindto.isAgreementFinance()) {
                memberjoindto.setAgreementFinance(true);
            } else {
                memberjoindto.setAgreementFinance(false);
            }
            if (memberjoindto.getAuth() == null || memberjoindto.getAuth().isEmpty()) {
                memberjoindto.setAuth("ROLE_MEMBER");
            }

            log.debug("Checking if user exists");
            if (memberRepository.existsById(memberjoindto.getId())) {
                throw new IllegalStateException("User already exists");
            }

            log.debug("Validating nickname");
            if (memberjoindto.getNickname() == null || memberjoindto.getNickname().length() > 50) {
                throw new IllegalArgumentException("Nickname must not be null and should not exceed 50 characters");
            }

            log.debug("Validating ID");
            if (memberjoindto.getId() == null || memberjoindto.getId().isEmpty()) {
                throw new IllegalStateException("User ID is required");
            }

            log.debug("Encoding password");
            String encodePassword = passwordEncoder.encode(memberjoindto.getPassword());

            log.debug("Building User entity");
            Member newMember = Member.builder()
                    .id(memberjoindto.getId())
                    .password(encodePassword)
                    .nickname(memberjoindto.getNickname())
                    .gender(String.valueOf(memberjoindto.getGender()))
                    .email(memberjoindto.getEmail())
                    .birth_year(memberjoindto.getBirth_year())
                    .auth(memberjoindto.getAuth())

                    .agreementInfo(memberjoindto.isAgreementInfo())
                    .agreementFinance(memberjoindto.isAgreementFinance())
                    .build();

            log.debug("Saving new user: {}", newMember);
            memberRepository.save(newMember);
            log.debug("User saved successfully");
        } catch (Exception e) {
            log.error("Error in MemberJoin: ", e);
            throw e;
        }
    }

    @Override
    public boolean checkAgree(boolean info, boolean finance, String id) {
        return true;
    }

    @Override
    public boolean updateUserAgreement(String id, Map<String, Boolean> agreementData) {
        boolean info = agreementData.get("info");
        boolean finance = agreementData.get("finance");
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));

        member.setAgreementInfo(info);
        member.setAgreementFinance(finance);

        memberRepository.save(member);
        boolean result = checkAgree(info, finance, id);
        return result;
    }

    private String generateRandomCode() {
        // 6자리 랜덤 숫자 생성 로직
        return String.format("%06d", new Random().nextInt(1000000));
    }

    @Override
    public String generateAndSaveVerificationCode(String email) {
        String verificationCode = generateRandomCode();
        verificationCodes.put(email, verificationCode);

        // 이메일 전송
        String subject = "Richable 인증 코드";
        String text = "귀하의 인증 코드는 " + verificationCode + " 입니다.";
        emailServiceImpl.sendSimpleMessage(email, subject, text);

        return verificationCode;
    }

    @Override
    public Map<String, String> findIdByEmail(String email) {
        Map<String, String> result = new HashMap<>();
        try {
            Member user = memberRepository.findByEmail(email);
            if (user != null) {
                generateAndSaveVerificationCode(email);
                result.put("message", "인증 코드가 이메일로 전송되었습니다.");
            } else {
                result.put("error", "해당 이메일로 등록된 사용자가 없습니다.");
            }
        } catch (Exception e) {
            log.error("Error in findIdByEmail: ", e);
            result.put("error", "서버 오류가 발생했습니다.");
        }
        return result;
    }

    @Override
    public VerificationResult verifyCode(String email, String code, VerificationType type) {
        log.info("Verifying code for email: {} and type: {}", email, type);

        validateInputs(email, code);
        String savedCode = getStoredVerificationCode(email);
        validateVerificationCode(code, savedCode);

        Member member = findMemberByEmail(email);

        if (type == VerificationType.ID) {
            return createIdVerificationResult(member);
        } else {
            return createPasswordVerificationResult(member);
        }
    }

    private void validateInputs(String email, String code) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code)) {
            log.error("Email or code is empty");
            throw new MemberException(ErrorCode.INVALID_INPUT, "이메일과 인증 코드는 필수입니다.");
        }
    }

    private String getStoredVerificationCode(String email) {
        String savedCode = verificationCodes.get(email);
        if (savedCode == null) {
            log.error("No verification code found for email: {}", email);
            throw new MemberException(ErrorCode.INVALID_VERIFICATION_CODE, "인증 코드가 만료되었습니다.");
        }
        return savedCode;
    }

    private void validateVerificationCode(String inputCode, String savedCode) {
        if (!savedCode.equals(inputCode)) {
            log.error("Verification code mismatch");
            throw new MemberException(ErrorCode.INVALID_VERIFICATION_CODE, "인증 코드가 일치하지 않습니다.");
        }
    }

    private Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            log.error("No member found with email: {}", email);
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "해당 이메일로 등록된 사용자를 찾을 수 없습니다.");
        }
        return member;
    }

    private VerificationResult createIdVerificationResult(Member member) {
        log.info("Creating ID verification result for member: {}", member.getId());
        return VerificationResult.builder()
                .verified(true)
                .message("인증이 성공적으로 완료되었습니다.")
                .id(member.getId())
                .build();
    }

    private VerificationResult createPasswordVerificationResult(Member member) {
        log.info("Creating password verification result for member: {}", member.getId());
        return VerificationResult.builder()
                .verified(true)
                .message("비밀번호를 재설정할 수 있습니다.")
                .canResetPassword(true)
                .build();
    }

    @Override
    public String findPwByEmail(String email) {
        String id = memberRepository.findByEmail(email).getId();
        if (id != null) {
            generateAndSaveVerificationCode(email);
            String result = "인증 코드가 이메일로 전송되었습니다.";
            return result;
        } else {
            throw new MemberException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    @Override
    public boolean resetPassword(String id, String newPassword) {
        Optional<Member> userOptional = memberRepository.findById(id);
        // Optional에서 실제 Member 객체를 추출
        Member member = userOptional.orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));

        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);

        // 업데이트된 객체를 저장
        memberRepository.save(member);
        return true;
    }

    @Override
    public Map<String, Object> getMemberInfoByToken(String token) {
        log.info("Fetching member info by token");
        try {
            String nickname = jwtProcessor.getNickname(token);
            Integer uid = jwtProcessor.getUid(token);
            log.info("Fetching info for nickname: {} and uid: {}", nickname, uid);

            MemberInfoDTO memberInfoDTO = memberInfoService.getUserInfoByNickname(nickname);
            if (memberInfoDTO == null) {
                throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "User information not found");
            }

            MemberAPI memberAPI = memberApiService.getMemberApiByUid(uid);
            if (memberAPI == null) {
                throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "API data not found");
            }

            Map<String, Object> stockInfo = new HashMap<>();
            stockInfo.put("base", memberAPI.getStock());
            stockInfo.put("token", memberAPI.getStockToken());
            stockInfo.put("secret", memberAPI.getStockSecret());
            stockInfo.put("app", memberAPI.getStockApp());

            Map<String, Object> coinInfo = new HashMap<>();
            coinInfo.put("base", memberAPI.getCoin());
            coinInfo.put("secret", memberAPI.getCoinSecret());
            coinInfo.put("app", memberAPI.getCoinApp());

            Map<String, Object> apiInfo = new HashMap<>();
            apiInfo.put("bank", memberAPI.getBank());
            apiInfo.put("stock", stockInfo);
            apiInfo.put("coin", coinInfo);

            Map<String, Object> data = new HashMap<>();
            data.put("nickname", memberInfoDTO.getNickname());
            data.put("email", memberInfoDTO.getEmail());
            data.put("img", memberInfoDTO.getImg());
            data.put("birthYear", memberInfoDTO.getBirthYear());
            data.put("gender", memberInfoDTO.getGender());
            data.put("certification", memberInfoDTO.isCertification());
            data.put("api", apiInfo);

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            log.info("Successfully fetched member info for nickname: {}", nickname);
            return response;
        } catch (MemberException e) {
            log.error("Failed to fetch member info: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching member info: {}", e.getMessage());
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to fetch member info");
        }
    }

    @Override
    public Map<String, Object> updateMemberInfo(Map<String, Object> updatedInfo, String token) {
        log.info("Updating member info");
        try {
            String tokenNickname = jwtProcessor.getNickname(token);

            // Verify that the token nickname matches the nickname in updatedInfo
            String updatedNickname = (String) updatedInfo.get("nickname");
            if (!tokenNickname.equals(updatedNickname)) {
                throw new MemberException(ErrorCode.INVALID_UNAUTHOR, "You can only update your own information");
            }

            // Find the existing member
            Member member = memberRepository.findByNickname(tokenNickname);

            // Update member information
            if (updatedInfo.containsKey("email")) {
                member.setEmail((String) updatedInfo.get("email"));
            }
            if (updatedInfo.containsKey("birthYear")) {
                member.setBirth_year((Integer) updatedInfo.get("birthYear"));
            }
            if (updatedInfo.containsKey("gender")) {
                member.setGender((String) updatedInfo.get("gender"));
            }
            if (updatedInfo.containsKey("img")) {
                member.setProfile((String) updatedInfo.get("img"));
            }

            // Save updated member
            Member updatedMember = memberRepository.save(member);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("nickname", updatedMember.getNickname());
            response.put("email", updatedMember.getEmail());
            response.put("img", updatedMember.getProfile());
            response.put("birthYear", updatedMember.getBirth_year());
            response.put("gender", updatedMember.getGender());

            log.info("Successfully updated member info for nickname: {}", tokenNickname);
            return response;
        } catch (MemberException e) {
            log.error("Failed to update member info: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating member info: {}", e.getMessage());
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update member info");
        }
    }

    @Override
    public boolean deleteMemberById(String nickname) {
        log.info("Attempting to delete member with nickname: {}", nickname);
        try {
            Member memberOptional = memberRepository.findByNickname(nickname);

            memberRepository.delete(memberOptional);
            log.info("Successfully deleted member with nickname: {}", nickname);

            return true;
        } catch (MemberException e) {
            log.error("Failed to delete member with nickname: {}. Error: {}", nickname, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting member with nickname: {}. Error: {}", nickname,
                    e.getMessage());
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to delete member");
        }
    }

    @Override
    public MemberDTO findByEmail(String email) {
        log.info("Attempting to find member by email: {}", email);
        try {
            Member member = memberRepository.findByEmail(email);
            if (member == null) {
                log.info("No member found with email: {}", email);
                return null;
            }

            MemberDTO memberDTO = MemberDTO.builder()
                    .uid(member.getUid())
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .gender(member.getGender().charAt(0))
                    .birth_year(String.valueOf(member.getBirth_year()))
                    .profile(member.getProfile())
                    .agreement_info(member.getAgreementInfo())
                    .agreement_finance(member.getAgreementFinance())
                    .is_mentor(member.getIsMentor())
                    .is_certification(member.getIsCertification())
                    .auth(member.getAuth())
                    .build();

            log.info("Successfully found and mapped member with email: {}", email);
            return memberDTO;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding member by email: {}. Error: {}", email, e.getMessage());
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to find member by email");
        }
    }

    @Override
    public Integer getCurrentUid() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            Optional<Member> member = memberRepository.findById(userDetails.getUsername());

            if (member.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_MEMEBER);
            }
            return member.get().getUid();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_MEMEBER);

        }
    }
}
