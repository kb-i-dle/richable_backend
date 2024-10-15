package com.idle.kb_i_dle_backend.domain.member.service;

import com.idle.kb_i_dle_backend.domain.member.dto.MemberBadgeDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberBadge;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberBadgeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final MemberService memberService;
    private final MemberBadgeRepository memberBadgeRepository;

    // 전체 뱃지 조회 메서드
    public List<MemberBadgeDTO> getAllBadges(String nickname) {
        // nickname으로 UserInfoEntity에서 uid를 조회
        Member member = memberService.findMemberByNickname(nickname);

        // uid로 모든 뱃지 정보 조회
        List<MemberBadge> badgeEntities = memberBadgeRepository.findByUidOrderByMainDesc(member);

        return badgeEntities.stream()
                .map(entity -> {
                    MemberBadgeDTO badgeDTO = new MemberBadgeDTO();
                    badgeDTO.setBadgeNo(member.getUid());  // 복합키에서 badgeNo 가져오기
                    badgeDTO.setName(entity.getBadge());
                    badgeDTO.setImg(entity.getBadgeNo().getImage());
                    badgeDTO.setDesc(entity.getBadgeNo().getDescription());  // 설명 설정
                    badgeDTO.setHaving(entity.getAchive());  // 보유 여부 설정
                    badgeDTO.setMain(entity.getMain());
                    return badgeDTO;
                }).collect(Collectors.toList());
    }

    // 달성 여부에 따라 뱃지 조회
    public List<MemberBadgeDTO> getUserBadges(String nickname, boolean isAchived) {

        Member member = memberService.findMemberByNickname(nickname);

        // uid로 뱃지 정보 조회
        List<MemberBadge> badgeEntities = memberBadgeRepository.findByUidAndAchiveOrderByMain(member,
                isAchived);

        return badgeEntities.stream()
                .map(entity -> {
                    MemberBadgeDTO badgeDTO = new MemberBadgeDTO();
                    badgeDTO.setBadgeNo(entity.getBadgeNo().getBadgeNo());  // 복합키에서 badgeNo 가져오기
                    badgeDTO.setName(entity.getBadge());
                    badgeDTO.setImg(entity.getBadgeNo().getImage());
                    badgeDTO.setDesc(entity.getBadgeNo().getDescription());
                    badgeDTO.setHaving(entity.getAchive());
                    badgeDTO.setMain(entity.getMain());
                    return badgeDTO;
                }).collect(Collectors.toList());
    }
}
