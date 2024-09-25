package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Table(name="bond" , catalog="asset")
public class Bond {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private int uid;

    private String itms_nm;

    private int cnt;

    private String prod_category;

    private int per_price;

    private int add_date;

    private int delete_date;
}
