package com.idle.kb_i_dle_backend.domain.member.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    Integer uid;
    String id;
    String password;
    String email;
    char gender;
    String birth_year;
    String profile;
    boolean agreement_info;
    boolean agreement_finance;
    boolean is_mentor;
    boolean is_certification;
    String nickname;
    String auth;


    public List<SimpleGrantedAuthority> getAuth() {
        return Arrays.stream(this.auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
