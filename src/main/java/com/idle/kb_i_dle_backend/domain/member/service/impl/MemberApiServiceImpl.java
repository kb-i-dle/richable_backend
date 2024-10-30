package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberApiRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberApiServiceImpl implements MemberApiService {

    @Autowired
    private MemberApiRepository memberApiRepository;

    public MemberAPI getMemberApiByUid(Integer uid) {
        return memberApiRepository.findByUid(uid);  // Optional로 단일 객체 반환
    }
}
