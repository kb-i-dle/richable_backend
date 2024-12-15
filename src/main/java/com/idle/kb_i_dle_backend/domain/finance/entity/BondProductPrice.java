package com.idle.kb_i_dle_backend.domain.finance.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="bond_list_price" , catalog="product")
public class BondProductPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    @Column(name = "isinCd")
    private String isinCd;

    @Column(name = "isinCdNm")
    private String isinCdNm;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "1m_b_price")
    private int oneMonthAgoPrice;

    @Column(name = "2m_b_price")
    private int twoMonthsAgoPrice;

    @Column(name = "3m_b_price")
    private int threeMonthsAgoPrice;

    @Column(name = "4m_b_price")
    private int fourMonthsAgoPrice;

    @Column(name = "5m_b_price")
    private int fiveMonthsAgoPrice;

    @Column(name = "6m_b_price")
    private int sixMonthsAgoPrice;


}
