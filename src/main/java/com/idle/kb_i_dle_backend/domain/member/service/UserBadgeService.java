package com.idle.kb_i_dle_backend.domain.member.service;

import com.idle.kb_i_dle_backend.domain.member.dto.MemberBadgeDTO;

import java.util.List;

public interface UserBadgeService {

    List<MemberBadgeDTO> getAllBadges(String nickname);

    List<MemberBadgeDTO> getUserBadges(String nickname, boolean isAchived);


}
