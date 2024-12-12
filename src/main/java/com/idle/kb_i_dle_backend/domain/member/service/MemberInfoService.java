package com.idle.kb_i_dle_backend.domain.member.service;

import com.idle.kb_i_dle_backend.domain.member.dto.MemberInfoDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;

public interface MemberInfoService {

    MemberInfoDTO getUserInfoByNickname(String nickname);

    MemberInfoDTO updateMemberInfo(MemberInfoDTO updatedUserInfo) throws Exception;

}
