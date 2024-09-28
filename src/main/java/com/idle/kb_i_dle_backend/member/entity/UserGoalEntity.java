package com.idle.kb_i_dle_backend.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
//Goal-entity 추가함
@Entity
@Getter
@Table(name = "user_goal", catalog = "user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;

    private Integer uid;
    private String category;
    private String title;
    private Long amount;
    private Timestamp set_date;
    private Integer priority;
    private Boolean is_achive;

    // User와의 관계 설정 (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "uid", insertable = false, updatable = false)
    private User user;
}

