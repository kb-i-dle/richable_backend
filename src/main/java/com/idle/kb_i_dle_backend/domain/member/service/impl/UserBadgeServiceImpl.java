package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.domain.member.dto.MemberBadgeDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberBadge;
import com.idle.kb_i_dle_backend.domain.member.exception.MemberException;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberBadgeRepository;

import java.util.List;
import java.util.stream.Collectors;

import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.member.service.UserBadgeService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBadgeServiceImpl implements UserBadgeService {

    private final MemberService memberService;
    private final MemberBadgeRepository memberBadgeRepository;

    // 전체 뱃지 조회 메서드
    public List<MemberBadgeDTO> getAllBadges(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new MemberException(ErrorCode.INVALID_INPUT, "Nickname cannot be null or empty");
        }
        // nickname으로 UserInfoEntity에서 uid를 조회
        Member member;
        try {
            member = memberService.findMemberByNickname(nickname);
            if (member == null) {
                throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "Member not found with nickname: " + nickname);
            }
        } catch (Exception e) {
            if (e instanceof MemberException) {
                throw e;
            }
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while finding member: " + e.getMessage());
        }

        // uid로 모든 뱃지 정보 조회
        List<MemberBadge> badgeEntities;
        try {
            badgeEntities = memberBadgeRepository.findByUidOrderByMainDesc(member);
            if (badgeEntities == null || badgeEntities.isEmpty()) {
                return List.of(); // 뱃지가 없는 경우 빈 리스트 반환
            }
        } catch (Exception e) {
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching badges: " + e.getMessage());
        }

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
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new MemberException(ErrorCode.INVALID_INPUT, "Nickname cannot be null or empty");
        }

        // nickname으로 member 조회
        Member member;
        try {
            member = memberService.findMemberByNickname(nickname);
            if (member == null) {
                throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "Member not found with nickname: " + nickname);
            }
        } catch (Exception e) {
            if (e instanceof MemberException) {
                throw e;
            }
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while finding member: " + e.getMessage());
        }

        // uid로 뱃지 정보 조회
        List<MemberBadge> badgeEntities;
        try {
            badgeEntities = memberBadgeRepository.findByUidAndAchiveOrderByMain(member, isAchived);
            if (badgeEntities == null || badgeEntities.isEmpty()) {
                return List.of(); // 뱃지가 없는 경우 빈 리스트 반환
            }
        } catch (Exception e) {
            throw new MemberException(ErrorCode.INTERNAL_SERVER_ERROR, "Error while fetching badges: " + e.getMessage());
        }

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
