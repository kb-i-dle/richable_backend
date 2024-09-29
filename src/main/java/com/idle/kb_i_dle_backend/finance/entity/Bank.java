package com.idle.kb_i_dle_backend.finance.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Table(name="bank" , catalog="asset")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    @Column(name = "uid")
    private int uid;

    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "account_num")
    private long accountNum;

    @Column(name = "prod_name")
    private String prodName;

    @Column(name = "prod_category")
    private String prodCategory;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "balance_amt")
    private long balanceAmt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delete_date")
    private Date deleteDate;

}
