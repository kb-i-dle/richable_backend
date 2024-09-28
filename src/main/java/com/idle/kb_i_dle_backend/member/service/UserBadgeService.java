package com.idle.kb_i_dle_backend.member.service;

import com.idle.kb_i_dle_backend.member.dto.UserBadgeDTO;
import com.idle.kb_i_dle_backend.member.entity.User;
import com.idle.kb_i_dle_backend.member.entity.UserBadgeEntity;
import com.idle.kb_i_dle_backend.member.repository.UserBadgeRepository;
import com.idle.kb_i_dle_backend.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeService {

    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private UserRepository userRepository;

    // 닉네임을 통해 모든 뱃지를 가져온다
    public List<UserBadgeDTO> getAllBadgesByNickname(String nickname) {
        // 유저 정보 가져오기
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }

        // 유저 ID로 뱃지 정보 가져옴
        return convertToUserBadgeDTOs(user.getUid());
    }

    // 유저의 UID로 모든 뱃지 정보 가져오기
    public List<UserBadgeDTO> getUserBadgesByUid(Integer uid) {
        return convertToUserBadgeDTOs(uid);
    }

    // 공통 로직: UserBadgeEntity -> UserBadgeDTO 변환
    private List<UserBadgeDTO> convertToUserBadgeDTOs(Integer uid) {
        List<UserBadgeEntity> badgeEntities = userBadgeRepository.findByUid(uid);

        // UserBadgeEntity를 UserBadgeDTO로 변환
        return badgeEntities.stream()
                .map(badge -> new UserBadgeDTO(
                        badge.getBadge_no(),
                        badge.getBadge(),      // 뱃지 이름
                        "자산 1억 달성시 수여",  // 설명 (필요에 따라 수정 가능)
                        badge.getBadge_achive() ? (byte) 1 : (byte) 0  // having 필드 처리
                ))
                .collect(Collectors.toList());
    }
}
