package com.idle.kb_i_dle_backend.domain.member.service.impl;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.member.dto.*;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.exception.MemberException;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberApiService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailServiceImpl emailServiceImpl;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProcessor jwtProcessor;
    @Mock
    private MemberInfoServiceImpl memberInfoService;
    @Mock
    private MemberApiService memberApiService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;
    private MemberJoinDTO testMemberJoinDTO;
    private LoginDTO testLoginDTO;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .uid(1)
                .id("testId")
                .password("encodedPassword")
                .nickname("testNick")
                .email("test@test.com")
                .gender("M")
                .birth_year(1990)
                .auth("ROLE_MEMBER")
                .build();

        testMemberJoinDTO = MemberJoinDTO.builder()
                .id("testId")
                .password("testPassword")
                .nickname("testNick")
                .email("test@test.com")
                .gender('M')
                .birth_year(1990)
                .auth("ROLE_MEMBER")
                .agreementInfo(true)
                .agreementFinance(true)
                .build();

        testLoginDTO = new LoginDTO("testId", "testPassword");
    }

    @Test
    @DisplayName("로그인 성공 시 토큰과 사용자 정보를 반환한다")
    @Transactional
    void login_Success() {
        // given
        Authentication mockAuth = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(testMember);
        given(mockAuth.getPrincipal()).willReturn(userDetails);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mockAuth);
        given(jwtProcessor.generateToken(anyString(), anyInt(), anyString(), anyString()))
                .willReturn("testToken");

        // when
        Map<String, Object> result = memberService.login(testLoginDTO);

        // then
        assertThat(result)
                .isNotNull()
                .satisfies(map -> {
                    assertThat(map.get("token")).isEqualTo("testToken");
                    assertThat(map.get("userInfo")).isInstanceOf(MemberInfoDTO.class);
                });
    }

    @Test
    @DisplayName("회원가입 성공 시 성공 메시지를 반환한다")
    @Transactional
    void registerMember_Success() {
        // given
        given(memberRepository.existsById(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        String result = memberService.registerMember(testMemberJoinDTO);

        // then
        assertThat(result).isEqualTo("User registered successfully");
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("ID 중복 확인 시 존재하는 ID는 true를 반환한다")
    @Transactional
    void checkDupl_True() {
        // given
        given(memberRepository.existsById("testId")).willReturn(true);

        // when
        boolean result = memberService.checkDupl("testId");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("UID로 회원 조회 시 존재하는 회원 정보를 반환한다")
    @Transactional
    void findMemberByUid_Success() {
        // given
        given(memberRepository.findByUid(1)).willReturn(testMember);

        // when
        Member result = memberService.findMemberByUid(1);

        // then
        assertThat(result)
                .isNotNull()
                .satisfies(member -> {
                    assertThat(member.getId()).isEqualTo("testId");
                    assertThat(member.getEmail()).isEqualTo("test@test.com");
                    assertThat(member.getNickname()).isEqualTo("testNick");
                });
    }

    @Test
    @DisplayName("존재하지 않는 UID로 조회 시 MemberException이 발생한다")
    @Transactional
    void findMemberByUid_NotFound() {
        // given
        given(memberRepository.findByUid(999)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> memberService.findMemberByUid(999))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("비밀번호 재설정 성공 시 true를 반환한다")
    @Transactional
    void resetPassword_Success() {
        // given
        given(memberRepository.findById("testId")).willReturn(Optional.of(testMember));
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        boolean result = memberService.resetPassword("testId", "newPassword");

        // then
        assertThat(result).isTrue();
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 비밀번호 재설정 시 EntityNotFoundException이 발생한다")
    @Transactional
    void resetPassword_UserNotFound() {
        // given
        given(memberRepository.findById("nonExistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.resetPassword("nonExistent", "newPassword"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("토큰으로 회원 정보 조회 시 상세 정보를 반환한다")
    @Transactional
    void getMemberInfoByToken_Success() {
        // given
        given(jwtProcessor.getNickname(anyString())).willReturn("testNick");
        given(jwtProcessor.getUid(anyString())).willReturn(1);

        MemberInfoDTO memberInfoDTO = new MemberInfoDTO(1, "testId", "test@test.com", "testNick", "ROLE_MEMBER");
        given(memberInfoService.getUserInfoByNickname("testNick")).willReturn(memberInfoDTO);

        MemberAPI memberAPI = new MemberAPI();
        given(memberApiService.getMemberApiByUid(1)).willReturn(memberAPI);

        // when
        Map<String, Object> result = memberService.getMemberInfoByToken("testToken");

        // then
        assertThat(result)
                .isNotNull()
                .satisfies(map -> {
                    Map<String, Object> data = (Map<String, Object>) map.get("data");
                    assertThat(data)
                            .containsKey("nickname")
                            .containsKey("email")
                            .containsKey("api");
                });
    }

    @Test
    @DisplayName("회원 정보 업데이트 성공 시 업데이트된 정보를 반환한다")
    @Transactional
    void updateMemberInfo_Success() {
        // given
        String token = "testToken";
        Map<String, Object> updatedInfo = new HashMap<>();
        updatedInfo.put("nickname", "testNick");
        updatedInfo.put("email", "new@test.com");
        updatedInfo.put("birthYear", 1991);

        given(jwtProcessor.getNickname(token)).willReturn("testNick");
        given(memberRepository.findByNickname("testNick")).willReturn(testMember);
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        Map<String, Object> result = memberService.updateMemberInfo(updatedInfo, token);

        // then
        assertThat(result)
                .isNotNull()
                .satisfies(map -> {
                    assertThat(map.get("email")).isEqualTo("new@test.com");
                    assertThat(map.get("nickname")).isEqualTo("testNick");
                });
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 null을 반환한다")
    @Transactional
    void findByEmail_NotFound() {
        // given
        given(memberRepository.findByEmail("nonexistent@test.com")).willReturn(null);

        // when
        MemberDTO result = memberService.findByEmail("nonexistent@test.com");

        // then
        assertThat(result).isNull();
    }
    // 추가할 테스트 메소드들:

    @Test
    @DisplayName("로그인 실패 시 MemberException이 발생한다")
    @Transactional
    void login_Fail() {
        // given
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new RuntimeException("Authentication failed"));

        // when & then
        assertThatThrownBy(() -> memberService.login(testLoginDTO))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("이미 존재하는 ID로 회원가입 시 MemberException이 발생한다")
    @Transactional
    void registerMember_DuplicateId() {
        // given
        given(memberRepository.existsById(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(testMemberJoinDTO))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("이메일로 ID 찾기 성공 시 인증 코드가 전송된다")
    @Transactional
    void findIdByEmail_Success() {
        // given
        given(memberRepository.findByEmail("test@test.com")).willReturn(testMember);

        // when
        Map<String, String> result = memberService.findIdByEmail("test@test.com");

        // then
        assertThat(result)
                .containsKey("message")
                .doesNotContainKey("error");
        assertThat(result.get("message")).isEqualTo("인증 코드가 이메일로 전송되었습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 ID 찾기 시 에러 메시지를 반환한다")
    @Transactional
    void findIdByEmail_NotFound() {
        // given
        given(memberRepository.findByEmail("nonexistent@test.com")).willReturn(null);

        // when
        Map<String, String> result = memberService.findIdByEmail("nonexistent@test.com");

        // then
        assertThat(result)
                .containsKey("error")
                .doesNotContainKey("message");
        assertThat(result.get("error")).isEqualTo("해당 이메일로 등록된 사용자가 없습니다.");
    }


    @Test
    @DisplayName("잘못된 인증 코드 검증 시 실패 응답을 반환한다")
    @Transactional
    void verifyCode_InvalidCode() {
        // given
        String email = "test@test.com";
        String wrongCode = "999999";

        // when
        Map<String, Object> result = memberService.verifyCode(email, wrongCode);

        // then
        assertThat(result)
                .containsEntry("verified", false)
                .containsEntry("message", "인증 코드가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("회원 삭제 성공 시 true를 반환한다")
    @Transactional
    void deleteMemberById_Success() {
        // given
        given(memberRepository.findByNickname("testNick")).willReturn(testMember);

        // when
        boolean result = memberService.deleteMemberById("testNick");

        // then
        assertThat(result).isTrue();
        then(memberRepository).should().delete(testMember);
    }
}