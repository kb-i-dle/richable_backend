package com.idle.kb_i_dle_backend.member.entity;

import com.idle.kb_i_dle_backend.finance.entity.Spot;
import lombok.*;

import javax.persistence.*;
import java.util.List;
//setter 어노테이셔 추가함
@Entity
@Getter
@Setter
@Table(name = "user_info", catalog = "user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    private String id;
    private String password;
    private String email;
    private String social;
    private Integer birth_year;
    private String gender;
    private String profile;

    private Boolean agreement_info;
    private Boolean agreement_finace;
    private Boolean is_mentor;
    private Boolean is_certification;

    private String nickname;
    private String auth;

    // User와 Spot의 양방향 관계 설정
    @OneToMany(mappedBy = "uid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots;
    // User와 UserApi의 관계 설정
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserApiEntity userApi;
}


