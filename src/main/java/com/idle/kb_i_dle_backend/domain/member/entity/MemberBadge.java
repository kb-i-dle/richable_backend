package com.idle.kb_i_dle_backend.domain.member.entity;


import com.idle.kb_i_dle_backend.global.entity.Badge;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@IdClass(MemberBadgeId.class)
@Table(name = "user_badge", catalog = "user_info")
public class MemberBadge {

    @Id
    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @Id
    @ManyToOne
    @JoinColumn(name = "badge_no")
    private Badge badgeNo;


    @Column(length = 100)
    private String badge;

    @Column(name = "badge_achive")
    private Boolean achive;

    @Column(name = "badge_main")
    private Boolean main;
}
