package com.idle.kb_i_dle_backend.domain.member.controller;


import com.idle.kb_i_dle_backend.domain.member.dto.profile.ResponseUploadProfileDTO;
import com.idle.kb_i_dle_backend.domain.member.service.MemberProfileService;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.dto.SuccessResponseDTO;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class UserProfileController {

    private final MemberService memberService;
    private final MemberProfileService memberProfileService;

    @PostMapping("/profile/upload")
    public ResponseEntity<SuccessResponseDTO> uploadProfileImage(@RequestParam("image") MultipartFile image)
            throws IOException {

        //이미지 파일인지 확인
        Integer uid = memberService.getCurrentUid();
        ResponseUploadProfileDTO responseUploadProfileDTO = new ResponseUploadProfileDTO(
                memberProfileService.registerProfileImage(uid, image));
        return new ResponseEntity<>(new SuccessResponseDTO(true, responseUploadProfileDTO), HttpStatus.OK);
    }


}
