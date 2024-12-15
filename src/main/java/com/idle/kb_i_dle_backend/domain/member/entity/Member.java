package com.idle.kb_i_dle_backend.domain.member.entity;

import com.idle.kb_i_dle_backend.domain.finance.entity.Spot;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberJoinDTO;
import javax.validation.constraints.NotNull;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_info", catalog = "user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    @NotNull
    @Column(name = "id",length = 50)
    private String id;

    @NotNull
    @Column(name = "password",length = 100)
    private String password;

    @NotNull
    @Column(name = "email",length = 100)
    private String email;

    @Column(name = "social",length = 10)
    private String social;

    @NotNull
    @Column(name = "birth_year")
    private Integer birth_year;

    @Column(name = "gender",length = 10)
    private String gender;

    @Column(name = "profile",length = 255)
    private String profile;

    @Column(name = "agreement_info")
    private Boolean agreementInfo;

    @Column(name = "agreement_finance")
    private Boolean agreementFinance;

    @Column(name = "is_mentor")
    @Builder.Default
    private Boolean isMentor=false;

    @Column(name = "is_certification")
    @Builder.Default
    private Boolean isCertification=false;

    @Column(name = "nickname",length = 100)
    private String nickname;

    @Column(name = "auth",length = 20)
    private String auth;

    // User와 Spot의 양방향 관계 설정
    @OneToMany(mappedBy = "uid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots;
    // User와 UserApi의 관계 설정
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberAPI memberAPI;

    public static Member from(MemberJoinDTO dto) {
        return Member.builder()
                .id(dto.getId())
                .password(dto.getPassword()) // 주의: 이 비밀번호는 아직 암호화되지 않았습니다.
                .email(dto.getEmail())
                .birth_year(dto.getBirth_year())
                .gender(String.valueOf(dto.getGender()))
                .nickname(dto.getNickname())
                .auth(dto.getAuth())
                .agreementInfo(dto.isAgreementInfo())
                .agreementFinance(dto.isAgreementFinance())
                .isMentor(false) // 기본값
                .isCertification(false) // 기본값
                .build();
    }


}
