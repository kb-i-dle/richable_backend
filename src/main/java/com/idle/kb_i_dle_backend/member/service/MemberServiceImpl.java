package com.idle.kb_i_dle_backend.member.service;

import com.idle.kb_i_dle_backend.member.dto.MemberDTO;
import com.idle.kb_i_dle_backend.member.dto.MemberJoinDTO;
import com.idle.kb_i_dle_backend.member.entity.User;
import com.idle.kb_i_dle_backend.member.mapper.MemberMapper;
import java.util.*;

import com.idle.kb_i_dle_backend.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // 임시로 인증 코드를 저장할 Map (실제 구현에서는 데이터베이스나 캐시를 사용해야 함)
    private Map<String, String> verificationCodes = new HashMap<>();

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, @Lazy PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean checkDupl(String id) {
        // Implement this method using memberMapper
        return memberMapper.checkDupl(id);
    }


    @Override
    @Transactional
    public MemberDTO getMember(String id) {
        // Implement this method using memberMapper
        return memberMapper.getMember(id);
    }

    @Override
    @Transactional
    public void MemberJoin(MemberJoinDTO memberjoindto) {
        if (memberMapper.checkDupl(memberjoindto.getId())) {
            throw new IllegalStateException("User already exists");
        }
        if (memberjoindto.getNickname() == null || memberjoindto.getNickname().length() > 50) {
            throw new IllegalArgumentException("Nickname must not be null and should not exceed 50 characters");
        }
        // Perform necessary checks and add try-catch blocks for any exceptions.
        if (memberjoindto.getId() == null || memberjoindto.getId().isEmpty()) {
            throw new IllegalStateException("User ID is required");
        }
        String encodePassword = passwordEncoder.encode(memberjoindto.getPassword());

        MemberJoinDTO newUser = new MemberJoinDTO(
                memberjoindto.getId(),
                encodePassword,
                memberjoindto.getNickname(),
                memberjoindto.getGender(),
                memberjoindto.getEmail(),
                memberjoindto.getBirth_year()
        );

        System.out.println("Inserting new user: " + newUser); // 디버깅을 위해 추가

        memberMapper.insertNewMember(newUser);
    }

    @Override
    public MemberDTO findById(String id) {
        // This method is used for JWT authentication
        return memberMapper.findById(id);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return false;
    }

    @Override
    public boolean checkAgree(boolean info, boolean finance, String id) {
        try {
            this.memberMapper.updateAgree(info, finance, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String findIdByEmail(String email) {
        return memberMapper.findIdByEmail(email);
    }

    @Override
    public String generateAndSaveVerificationCode(String email) {
        String verificationCode = generateRandomCode();
        verificationCodes.put(email, verificationCode);

        // 이메일 전송
        String subject = "Richable 인증 코드";
        String text = "귀하의 인증 코드는 " + verificationCode + " 입니다.";
        emailService.sendSimpleMessage(email, subject, text);

        return verificationCode;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        return savedCode != null && savedCode.equals(code);
    }

    private String generateRandomCode() {
        // 6자리 랜덤 숫자 생성 로직
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean resetPassword(String id, String newPassword) {

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 데이터베이스에 새 비밀번호 저장
        return memberMapper.resetPassword(id, encodedPassword) > 0;
    }

    // 마이페이지 회원 구간 만듬

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean deleteMemberById(String id) {
        // ID로 회원 조회 후 삭제
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 회원 삭제
        userRepository.delete(user);
        return true;
    }
}