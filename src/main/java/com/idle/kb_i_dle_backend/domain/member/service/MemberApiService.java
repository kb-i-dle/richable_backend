package com.idle.kb_i_dle_backend.domain.member.service;

import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;

public interface MemberApiService {
    MemberAPI getMemberApiByUid(Integer uid);
}
