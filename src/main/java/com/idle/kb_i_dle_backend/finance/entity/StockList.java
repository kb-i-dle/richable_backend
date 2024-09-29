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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_list", catalog = "product")
public class StockList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index; // 색인, 기본 키, 자동 증가

    @Column(name = "standard_code", length = 50, nullable = false)
    private String standardCode; // ISIN 코드, 필수값

    @Column(name = "short_code", length = 50, nullable = false)
    private String shortCode; // 단축코드, 필수값

    @Column(name = "kr_stock_nm", length = 255, nullable = false)
    private String krStockNm; // 종목명, 필수값

    @Column(name = "kr_stock_abbr", length = 100, nullable = false)
    private String krStockAbbr; // 종목명_축약, 필수값

    @Column(name = "market", length = 100, nullable = false)
    private String market; // 시장구분, 필수값

    @Column(name = "price", nullable = true)
    private Integer price; // 가격(종가), 필수값 아님

    // StockList와 ManyToOne 관계를 설정하고, pdno와 short_code를 기준으로 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_code", referencedColumnName = "standardCode", insertable = false, updatable = false)
    private StockListPrice stockListPrice;
}
