package com.idle.kb_i_dle_backend.domain.finance.entity;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank", catalog = "asset")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @Column(name = "org_code", length = 100)
    private String orgCode;

    @Column(name = "account_num")
    private String accountNum;

    @Column(name = "prod_name",length = 100)
    private String name;

    @NotNull
    @Column(name = "prod_category", length = 100)
    private String category;

    @Column(name = "account_type", length = 100)
    private String accountType;

    @Column(name = "currency_code", length = 10)
    private String currencyCode;

    @NotNull
    @Column(name = "balance_amt")
    private Long balanceAmt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    // 엔티티가 처음 영속화될 때(addDate를 자동으로 설정)
    @PrePersist
    protected void onCreate() {
        this.addDate = new Date();  // 현재 시간을 자동으로 설정
    }
}
