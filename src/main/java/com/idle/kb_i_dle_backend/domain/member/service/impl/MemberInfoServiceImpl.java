package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.domain.member.dto.MemberApiDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberInfoDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {

    @Autowired
    private MemberRepository memberRepository;

    // 기존 사용자의 정보를 가져오는 메서드
    public MemberInfoDTO getUserInfoByNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);

        if (member != null) {
            // UserApiEntity -> UserApiDTO 변환
            MemberApiDTO memberApiDTO = null;
            if (member.getMemberAPI() != null) {
                MemberAPI memberAPI = member.getMemberAPI();
                memberApiDTO = new MemberApiDTO(
                        memberAPI.getMember().getUid(),
                        memberAPI.getBank(),
                        memberAPI.getStock(),
                        memberAPI.getStockToken(),
                        memberAPI.getStockSecret(),
                        memberAPI.getStockApp(),
                        memberAPI.getCoin(),
                        memberAPI.getCoinApp(),
                        memberAPI.getCoinSecret()
                );
            }

            // User -> UserInfoDTO 변환
            return new MemberInfoDTO(
                    member.getUid(),
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getAuth(),
                    member.getProfile(),
                    member.getBirth_year(),
                    member.getGender(),
                    Boolean.TRUE.equals(member.getIsCertification()),
                    memberApiDTO  // 변환된 UserApiDTO 포함
            );
        }

        return null;  // 사용자 정보를 찾지 못한 경우 null 반환
    }

    // 사용자 정보를 업데이트하는 메서드 추가
    public MemberInfoDTO updateMemberInfo(MemberInfoDTO updatedUserInfo) throws Exception {
        // 닉네임을 사용하여 사용자 찾기
        Member member = memberRepository.findByNickname(updatedUserInfo.getNickname());

        if (member == null) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }

        // UserInfoDTO의 정보를 User 엔티티에 반영
        member.setEmail(updatedUserInfo.getEmail());  // setEmail()으로 수정
        member.setProfile(updatedUserInfo.getImg());  // 'img' 필드를 'profile'로 매핑
        member.setBirth_year(updatedUserInfo.getBirthYear());
        member.setGender(updatedUserInfo.getGender());

        // 사용자 정보 저장
        memberRepository.save(member);

        // 업데이트된 사용자 정보를 반환 (UserInfoDTO 변환)
        return new MemberInfoDTO(
                member.getUid(),
                member.getId(),
                member.getNickname(),
                member.getAuth(),
                member.getEmail(),
                member.getProfile(),
                member.getBirth_year(),
                member.getGender(),
                Boolean.TRUE.equals(member.getIsCertification()),
                null  // 변환된 UserApiDTO 포함
        );
    }

    public MemberAPI getMemberApiByUid(Integer uid) {
        return null;
    }
}
