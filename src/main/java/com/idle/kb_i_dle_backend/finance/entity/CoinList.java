package com.idle.kb_i_dle_backend.finance.entity;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "coin_list", catalog = "product")
public class CoinList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 기본 키, 자동 증가

    @Column(name = "coin_name", length = 10, nullable = false)
    private String coinName; // 코인 이름, 필수값

    @Column(name = "closing_price", length = 50, nullable = false)
    private String closingPrice; // 종가, 필수값

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updateAt; // 가격 갱신 시간, 필수값

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_name", referencedColumnName = "coin_name", insertable = false, updatable = false)
    private CoinListPrice coinListPrice;
}
