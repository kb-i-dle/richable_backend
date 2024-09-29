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
@Table(name="coin_list_price" , catalog="product")
public class CoinListPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;

    @Column(name = "coin_name")
    private String coinName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "1m_b_price")
    private String oneMonthAgoPrice;

    @Column(name = "2m_b_price")
    private String twoMonthsAgoPrice;

    @Column(name = "3m_b_price")
    private String threeMonthsAgoPrice;

    @Column(name = "4m_b_price")
    private String fourMonthsAgoPrice;

    @Column(name = "5m_b_price")
    private String fiveMonthsAgoPrice;

    @Column(name = "6m_b_price")
    private String sixMonthsAgoPrice;
}
