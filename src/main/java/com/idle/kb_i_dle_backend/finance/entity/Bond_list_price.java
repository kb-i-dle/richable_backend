package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="bond_list_price" , catalog="product")
public class Bond_list_price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private String isin_cd;

    private String isinCdNm;

    private int date;

    private int oneMonthAgoPrice;

    private int twoMonthsAgoPrice;

    private int threeMonthsAgoPrice;

    private int fourMonthsAgoPrice;

    private int fiveMonthsAgoPrice;

    private int sixMonthsAgoPrice;
}
