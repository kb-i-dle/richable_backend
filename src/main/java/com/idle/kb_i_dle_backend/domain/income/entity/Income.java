package com.idle.kb_i_dle_backend.domain.income.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "income" , catalog = "asset")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @NotNull
    @Column(length = 100)
    private String type;

    @NotNull
    private Long amount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @Column(nullable = true)
    private String descript;

    @Column(nullable = true)
    private String memo;

    @Column(name = "account_num",nullable = true)
    private Long accountNum;

//    // 엔티티가 처음 영속화될 때(Date를 자동으로 설정)
//    @PrePersist
//    protected void onCreate() {
//        this.date = new Date();  // 현재 시간을 자동으로 설정
//    }
}
