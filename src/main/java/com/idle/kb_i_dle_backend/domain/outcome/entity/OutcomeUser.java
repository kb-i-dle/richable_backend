package com.idle.kb_i_dle_backend.domain.outcome.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;

import java.math.BigInteger;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outcome_user",catalog = "outcome")
public class OutcomeUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @NotNull
    @Column(name = "outcome_expenditure_category")  // 실제 테이블의 컬럼과 매핑
    private String category;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @NotNull
    private Long amount;

    private String descript;

    private String memo;

    @Column(name = "account_num")  // 실제 테이블의 컬럼과 매핑
    private Long accountNum;

//    // 엔티티가 처음 영속화될 때(Date를 자동으로 설정)
//    @PrePersist
//    protected void onCreate() {
//        this.date = new Date();  // 현재 시간을 자동으로 설정
//    }
}
