package com.idle.kb_i_dle_backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
//새로 수정 9.26
@Data
@AllArgsConstructor
@Getter
@Setter
public class MemberInfoDTO {
    private Integer uid;
    private String id;
    private String email;
    private String nickname;
    private String auth;          // 추가 필드
    private String img;        // 추가 필드
    private Integer birthYear;  // 추가 필드
    private String gender;     // 추가 필드
    private boolean certification;
    private MemberApiDTO api;

    // 필요한 필드만 사용하는 생성자 (이 경우 uid, id, nickname, auth)

    public MemberInfoDTO(Integer uid, String id, String email, String nickname, String auth) {
        this.uid = uid;
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.auth = auth;
        // 나머지 필드는 기본 값으로 설정
        this.img = null;
        this.birthYear = null;
        this.gender = null;
        this.certification = false;
        this.api = null;
    }
}
