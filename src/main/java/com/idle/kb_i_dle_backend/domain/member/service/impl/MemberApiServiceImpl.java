package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.domain.member.entity.MemberAPI;
import com.idle.kb_i_dle_backend.domain.member.exception.MemberException;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberApiRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberApiService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberApiServiceImpl implements MemberApiService {

    @Autowired
    private MemberApiRepository memberApiRepository;

    public MemberAPI getMemberApiByUid(Integer uid) {
        MemberAPI memberAPI = memberApiRepository.findByUid(uid);
        if (memberAPI == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND, "Member API not found for uid: " + uid);
        }
        return memberAPI;

    }
}
