package com.idle.kb_i_dle_backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//badgeNo함
@Data
@AllArgsConstructor
public class UserBadgeDTO {
    private Integer badgeNo;
    private String name;
    private String desc;
    private Byte having;
}
