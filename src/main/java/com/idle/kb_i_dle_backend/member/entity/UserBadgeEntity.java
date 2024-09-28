package com.idle.kb_i_dle_backend.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
//badge-entity 추가함
@Entity
@Getter
@Table(name = "user_badge", catalog = "user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserBadgeEntityPK.class)
public class UserBadgeEntity implements Serializable {

    @Id
    private Integer uid;

    @Id
    private Integer badge_no;

    private String badge;
    private Boolean badge_achive;
    private Boolean badge_main;

    // User와의 관계 설정 (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "uid", insertable = false, updatable = false)
    private User user;
}
