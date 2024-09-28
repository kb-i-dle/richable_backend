package com.idle.kb_i_dle_backend.member.dto;

import com.idle.kb_i_dle_backend.member.dto.UserApiDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDTO {
    private Integer uid;
    private String id;
    private String nickname;
    private String auth;
    private String email;
    private Integer birthYear;
    private String gender;
    private Boolean certification;
    private String img;  // img 필드 추가
    private UserApiDTO api;

    // img 필드를 초기화함.
    // 필요한 필드만 받는 생성자
    public UserInfoDTO(Integer uid, String id, String nickname, String auth) {
        this.uid = uid;
        this.id = id;
        this.nickname = nickname;
        this.auth = auth;
        this.email = null;
        this.birthYear = null;
        this.gender = null;
        this.certification = null;
        this.img = null;
        this.api = null;
    }

    // 모든 필드를 받는 생성자
    public UserInfoDTO(Integer uid, String id, String nickname, String auth, String email,
                       Integer birthYear, String gender, Boolean certification, String img, UserApiDTO api) {
        this.uid = uid;
        this.id = id;
        this.nickname = nickname;
        this.auth = auth;
        this.email = email;
        this.birthYear = birthYear;
        this.gender = gender;
        this.certification = certification;
        this.img = img;  // img 필드를 생성자에 추가
        this.api = api;
    }
}
