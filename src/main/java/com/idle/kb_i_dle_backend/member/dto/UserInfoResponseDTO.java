package com.idle.kb_i_dle_backend.member.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//ResponseDTO 만들었음
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDTO {
    private String nickname;
    private String email;
    private String img;
    private String birthYear;
    private String gender;
    private boolean certification;
    private UserApiDTO api;
}
