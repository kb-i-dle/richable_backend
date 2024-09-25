package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Table;
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

    private int uid;

    private String org_code;

    private long account_num;

    private String prod_name;

    private String prod_category;

    private String account_type;

    private String currency_code;

    private long balance_amt;

    private int add_date;

    private int delete_date;

}
