package com.idle.kb_i_dle_backend.finance.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="coin" , catalog="asset")
public class Coin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance")
    private double balance;

    @Column(name = "avg_buy_price")
    private double avgBuyPrice;

    @Column(name = "unit_currency")
    private String unitCurrency;

    @Column(name = "prod_category")
    private String prodCategory;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delete_date")
    private Date deleteDate;

    // StockList와 ManyToOne 관계를 설정하고, pdno와 short_code를 기준으로 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", referencedColumnName = "coin_name", insertable = false, updatable = false)
    private CoinList coinList;
}
