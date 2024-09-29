package com.idle.kb_i_dle_backend.finance.entity;

import java.util.Date;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="stock" , catalog="asset")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "pdno")
    private Integer pdno;

    @Column(name = "prdt_name")
    private String prdt_name;

    @Column(name = "hldg_qty")
    private Integer hldg_qty;

    @Column(name = "prod_category")
    private String prod_category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delete_date")
    private Date deleteDate;



    // StockList와 ManyToOne 관계를 설정하고, pdno와 short_code를 기준으로 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdno", referencedColumnName = "short_code", insertable = false, updatable = false)
    private StockList stockList;
}
