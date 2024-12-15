package com.idle.kb_i_dle_backend.domain.member.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {
    public String registerProfileImage(Integer uid, MultipartFile file) throws IOException;

}
