package com.idle.kb_i_dle_backend.member.service;

import com.idle.kb_i_dle_backend.member.dto.MemberDTO;
import com.idle.kb_i_dle_backend.member.dto.MemberJoinDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public interface    MemberService {
    boolean checkDupl(String id);

    MemberDTO getMember(String id);

    void MemberJoin(MemberJoinDTO memberjoindto);

    // Add this method if it doesn't exist
    MemberDTO findById(String id);

    boolean checkPassword(String rawPassword, String encodedPassword);

    boolean checkAgree(boolean info, boolean finance,String id);

    String findIdByEmail(String email);

    String generateAndSaveVerificationCode(String email);

    boolean verifyCode(String email, String code);

    boolean resetPassword(String id, String newPassword);
    //회원 삭제 메서드 만들었음
    boolean deleteMemberById(String id);
}
