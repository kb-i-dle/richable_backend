package com.idle.kb_i_dle_backend.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberBadgeDTO {
    private Integer badgeNo;
    private String name;
    private String img;
    private String desc;
    private boolean having;
    private Boolean main;

    // 기본 생성자
    public MemberBadgeDTO() {
    }

}
