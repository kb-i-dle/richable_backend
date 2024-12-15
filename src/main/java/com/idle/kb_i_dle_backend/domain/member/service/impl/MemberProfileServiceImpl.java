package com.idle.kb_i_dle_backend.domain.member.service.impl;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberProfileService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.service.S3Service;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberProfileServiceImpl implements MemberProfileService {

    private static final String PROFILE = "profileImages";
    private final S3Service s3Service;
    private final MemberService memberService;


    @Override
    @Transactional
    public String registerProfileImage(Integer uid, MultipartFile file) {
        //멤버확인
        Member member = memberService.findMemberByUid(uid);

        //멤버의 id값을 가져옴
        //id값으로 file이름 변경

        //프로필 이미지가 존재하지 않다면
        if (member.getProfile() == null || member.getProfile().isBlank()) {
            String profileUrl = s3Service.uploadProfile(file, member.getId());
            member.setProfile(profileUrl);
            return profileUrl;
        }
        //프로필 이미지가 존재한다면
        else if (!member.getProfile().isBlank()) {

            String existImageUrl = member.getProfile();
            String existingFileName = existImageUrl.substring(
                    existImageUrl.lastIndexOf(PROFILE));
            s3Service.deleteFile(existingFileName);
            String profileUrl = s3Service.uploadProfile(file, member.getId());
            member.setProfile(profileUrl);
            return profileUrl;
        }
        return "";
    }
}
