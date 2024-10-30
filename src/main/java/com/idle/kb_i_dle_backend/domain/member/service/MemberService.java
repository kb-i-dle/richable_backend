package com.idle.kb_i_dle_backend.domain.member.service;

import com.idle.kb_i_dle_backend.domain.member.dto.LoginDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberJoinDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface MemberService {

    Map<String, Object> login(LoginDTO loginDTO);

    Map initiateNaverLogin(HttpServletRequest request);

    Map processNaverCallback(String code, String state);

    String registerMember(MemberJoinDTO signupDTO);

    boolean updateUserAgreement(String id, Map<String, Boolean> agreementData);

    boolean checkDupl(String id);

    Member findMemberByUid(int id);

    Member findMemberByNickname(String nickname);

    void MemberJoin(MemberJoinDTO memberjoindto);

    boolean checkAgree(boolean info, boolean finance, String id);

    Map<String, String> findIdByEmail(String email);

    String findPwByEmail(String email);

    String generateAndSaveVerificationCode(String email);

    Map<String, Object> verifyCode(String email, String code);

    boolean resetPassword(String id, String newPassword);

    Map<String, Object> getMemberInfoByToken(String token);

    Map<String, Object> updateMemberInfo(Map<String, Object> updatedInfo, String token);

    boolean deleteMemberById(String id);

    MemberDTO findByEmail(String email);

    Integer getCurrentUid();
}
