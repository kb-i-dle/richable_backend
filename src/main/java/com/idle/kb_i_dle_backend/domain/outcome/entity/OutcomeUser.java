package com.idle.kb_i_dle_backend.domain.outcome.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outcome_user", catalog = "outcome")
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

    public OutcomeUser(Member uid, String category, Date date, Long amount) {
        this.uid = uid;
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

}
