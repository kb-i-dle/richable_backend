package com.idle.kb_i_dle_backend.domain.member.controller;

import com.idle.kb_i_dle_backend.common.dto.ResponseDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.AuthResultDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.CustomUser;
import com.idle.kb_i_dle_backend.domain.member.dto.LoginDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberInfoDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberJoinDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.service.MemberApiService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberInfoService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

@Slf4j
@RestController
@RequestMapping("/member")
@PropertySource({"classpath:/application.properties"})
public class MemberController {

    private final AuthenticationManager authenticationManager;
    private final JwtProcessor jwtProcessor;
    private final MemberService memberService;
    private final MemberInfoService memberInfoService;
    private final MemberApiService memberApiService;

    public MemberController(AuthenticationManager authenticationManager, JwtProcessor jwtProcessor,
                            MemberService memberService, MemberInfoService memberInfoService,MemberApiService memberApiService) {
        this.authenticationManager = authenticationManager;
        this.jwtProcessor = jwtProcessor;
        this.memberService = memberService;
        this.memberInfoService = memberInfoService;
        this.memberApiService = memberApiService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        // Perform authentication
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getId(), loginDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Assuming you have a method to fetch UserInfoDTO (e.g., from the authenticated user details)
            MemberInfoDTO userInfo = getUserInfoFromAuthentication(authentication);
            // Generate JWT token with uid
            String jwtToken = jwtProcessor.generateToken(userInfo.getId(), userInfo.getUid(), userInfo.getNickname(),userInfo.getEmail());
            log.error("jwtProcessor 값 확인 : "+userInfo.getId()+" / "+userInfo.getUid()+" / "+ userInfo.getNickname());
            // Return the AuthResultDTO with token and user info
            return ResponseEntity.ok(new AuthResultDTO(jwtToken, userInfo));

        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    // Helper method to retrieve user information from the authentication object
    private MemberInfoDTO getUserInfoFromAuthentication(Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        MemberDTO member = customUser.getMember();  // Retrieve the MemberDTO object
        log.error("memberDTO 확인 : "+member.getUid()+" / "+member.getId()+" / "+member.getEmail()+" / "+member.getNickname()+" / "+member.getAuth().toString());
        return new MemberInfoDTO(member.getUid(), member.getId(),member.getEmail(), member.getNickname(), member.getAuth().toString());
    }

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.redirect.uri}")
    private String redirectUri;

    @GetMapping("/naverlogin")
    public ResponseEntity<?> naverlogin(HttpServletRequest request) throws Exception {
        // 상태 토큰으로 사용할 랜덤 문자열 생성
        String state = generateState();

        // 세션 또는 별도의 저장 공간에 상태 토큰을 저장
        HttpSession session = request.getSession();
        session.setAttribute("naverState", state);
        // 디버깅을 위한 세션 상태 로깅
        log.error("생성된 상태 토큰: " + state);
        log.error("세션 ID: " + session.getId());

        String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8")
                + "&state=" + state;
        log.error("naverAuthUrl: {}", naverAuthUrl);

        return ResponseEntity.ok(Map.of("redirectUrl", naverAuthUrl, "state", state));
    }

    public String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @GetMapping("/naverCallback")
    public ResponseEntity<?> naverCallback(@RequestParam(required = false) String code,
                                           @RequestParam(required = false) String state,HttpServletRequest request) throws Exception {

        log.info("Received callback - code: {}, state: {}", code, state);

        // 최종 인증 값인 접근 토큰을 발급
        String tokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&state=" + state;

        String authtoken = null;
        String accessToken = null;
        int retryCount = 0;
        int maxRetries = 3;

        while (accessToken == null && retryCount < maxRetries) {
            try {
                authtoken = getHttpResponse(tokenUrl);
                if (authtoken == null || authtoken.isEmpty()) {
                    throw new Exception("Empty auth token received");
                }

                // JSON 파싱
                JSONObject tokenJson = new JSONObject(authtoken);
                accessToken = tokenJson.getString("access_token");

                if (accessToken == null || accessToken.isEmpty()) {
                    throw new Exception("Empty access token received");
                }
            } catch (Exception e) {
                log.warn("Failed to get access token, attempt {} of {}", retryCount + 1, maxRetries, e);
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("Failed to get access token after {} attempts", maxRetries);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to authenticate with Naver");
                }
                // 재시도 전 잠시 대기
                Thread.sleep(1000);
            }
        }

        log.info("Access Token obtained successfully");

        // 사용자 프로필 정보 조회
        JSONObject userProfile = getUserProfile(accessToken);
        log.info("User Profile: {}", userProfile.toString());

        // 여기서 userProfile을 사용하여 회원가입 또는 로그인 처리
        // 사용자 정보 처리 (회원가입 또는 로그인)
        JSONObject responseObj = userProfile.getJSONObject("response");
        String email = responseObj.getString("email");
        String nickname = responseObj.getString("nickname");

        // 사용자 정보로 MemberDTO 생성 또는 조회
        MemberDTO memberDTO = memberService.findByEmail(email);
        if (memberDTO == null) {
            // 새 사용자 등록 로직
            MemberJoinDTO joinDTO = MemberJoinDTO.builder()
                    .email(email)
                    .nickname(nickname)
                    // 다른 필요한 정보 설정
                    .build();
            memberService.MemberJoin(joinDTO);
            memberDTO = memberService.findByEmail(email);
        }

        // JWT 토큰 생성
        String jwtToken = jwtProcessor.generateToken(memberDTO.getId(), memberDTO.getUid(), memberDTO.getNickname(), memberDTO.getEmail());
        // JWT 토큰을 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("jwtToken", jwtToken);

        // 세션에 JWT 토큰이 제대로 저장되었는지 확인
        String storedToken = (String) session.getAttribute("jwtToken");
        if (storedToken == null || !storedToken.equals(jwtToken)) {
            log.error("Failed to store JWT token in session");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete authentication process");
        }
        log.error("JWT token successfully stored in session");

        // 홈페이지로 리다이렉트
        String homeUrl = "http://localhost:5173"; // 프론트엔드 홈 URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, homeUrl)
                .build();
    }

    private String getHttpResponse(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
    private JSONObject getUserProfile(String accessToken) throws Exception{
        String profileUrl = "https://openapi.naver.com/v1/nid/me";
        URL url = new URL(profileUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        return new JSONObject(response.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<?> signup(@RequestBody MemberJoinDTO signupDTO) {
        try {
            memberService.MemberJoin(signupDTO);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  // Log the error for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @GetMapping("/checkDupl/{id}")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateUsername(@PathVariable String id) {
        boolean exists = memberService.checkDupl(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/terms/{id}")
    public ResponseEntity<Map<String, Boolean>> updateUserAgreement(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> agreementData) {

        boolean info = agreementData.get("info");
        boolean finance = agreementData.get("finance");

        boolean success = memberService.checkAgree(info, finance, id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/findid")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String id = memberService.findIdByEmail(email);
        if (id != null) {
            String verificationCode = memberService.generateAndSaveVerificationCode(email);
            System.out.println(verificationCode);
            log.info("verification code: " + verificationCode);
            // TODO: 이메일로 verificationCode 전송 로직 구현
            return ResponseEntity.ok(Map.of("message", "인증 코드가 이메일로 전송되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 이메일로 등록된 아이디가 없습니다."));
        }
    }

    @PostMapping("/findid/auth")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        boolean isValid = memberService.verifyCode(email, code);
        if (isValid) {
            String id = memberService.findIdByEmail(email);
            return ResponseEntity.ok(Map.of("id", id));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "잘못된 인증 코드입니다."));
        }
    }

    @PostMapping("/find/pw/auth")
    public ResponseEntity<?> findPw(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String id = memberService.findIdByEmail(email);
        if (id != null) {
            String verificationCode = memberService.generateAndSaveVerificationCode(email);
            System.out.println(verificationCode);
            log.info("verification code: " + verificationCode);
            // TODO: 이메일로 verificationCode 전송 로직 구현
            return ResponseEntity.ok(Map.of("message", "인증 코드가 이메일로 전송되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 이메일로 등록된 아이디가 없습니다."));
        }
    }

    @PostMapping("/find_pw_auth")
    public ResponseEntity<?> verifyCode2(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        boolean isValid = memberService.verifyCode(email, code);
        if (isValid) {
            String id = memberService.findIdByEmail(email);
            return ResponseEntity.ok(Map.of("id", id));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "잘못된 인증 코드입니다."));
        }
    }

    @PostMapping("/set/pw")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        String newPassword = payload.get("newPassword");
        boolean success = memberService.resetPassword(id, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 성공적으로 재설정되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호 재설정 중 오류가 발생했습니다."));
        }
    }
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfoByNickname(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String nickname = jwtProcessor.getNickname(token);
        Integer uid = jwtProcessor.getUid(token);
        log.error("nickname"+nickname+" / "+"uid"+uid);

        // 사용자 정보 가져오기
        MemberInfoDTO memberInfoDTO = memberInfoService.getUserInfoByNickname(nickname);

        // 사용자 정보가 없으면 404 반환
        if (memberInfoDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보가 없습니다.");
        }

        // uid를 사용하여 UserApiEntity 가져오기 (단일 객체로 처리)
        MemberAPI memberAPI = memberApiService.getMemberApiByUid(uid);

        // userApiEntity가 null인 경우 처리
        if (memberAPI == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("API 데이터가 없습니다.");
        }

        // 본인 여부 판단
//        if (tokenNickname.equals(nickname)) {
            // 본인일 경우 모든 정보 반환
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

            ResponseDTO successResponse = new ResponseDTO(true, response);
            return ResponseEntity.ok(successResponse);
//        } else {
            // 본인이 아닌 경우 제한된 정보만 반환
//            Map<String, Object> limitedData = new HashMap<>();
//            limitedData.put("nickname", memberInfoDTO.getNickname());
//            limitedData.put("img", memberInfoDTO.getImg());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("data", limitedData);
//
//            ResponseDTO successResponse = new ResponseDTO(true, response);
//            return ResponseEntity.ok(successResponse);
//        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateMemberInfo(@RequestBody MemberInfoDTO updatedMemberInfo, HttpServletRequest request) {
        // JWT에서 현재 사용자의 닉네임 가져오기
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String tokenNickname = jwtProcessor.getNickname(token);

        // 본인 여부 판단
        if (!tokenNickname.equals(updatedMemberInfo.getNickname())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자신의 정보만 수정할 수 있습니다.");
        }

        // 사용자 정보 업데이트 로직
        try {
            MemberInfoDTO updatedInfo = memberInfoService.updateMemberInfo(updatedMemberInfo);

            // 성공 응답 반환
            Map<String, Object> data = new HashMap<>();
            data.put("nickname", updatedInfo.getNickname());
            data.put("email", updatedInfo.getEmail());
            data.put("img", updatedInfo.getImg());
            data.put("birthYear", updatedInfo.getBirthYear());
            data.put("gender", updatedInfo.getGender());

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            ResponseDTO successResponse = new ResponseDTO(true, response);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원정보 수정 중 오류가 발생했습니다.");
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable String id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        String nickname = jwtProcessor.getNickname(token);

        // id로 회원 삭제 처리
        boolean isDeleted = memberService.deleteMemberById(id);  // boolean 타입으로 수정

        if (isDeleted) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);  // 삭제된 ID를 응답에 포함

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            ResponseDTO responseDTO = new ResponseDTO(true, response);
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(404).body("회원 정보를 찾을 수 없습니다.");
        }
    }


}