package com.idle.kb_i_dle_backend.domain.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="bond_list" , catalog="product")
public class BondProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "crno")
    private String crno;

    @Column(name = "scrsItmsKcd")
    private String scrsItmsKcd;

//    @Column(name = "isinCd")
//    private String isinCd;

    @Column(name = "scrsItmsKcdNm")
    private String scrsItmsKcdNm;

    @Column(name = "bondIsurNm")
    private String bondIsurNm;

    @Column(name = "isinCdNm")
    private String isinCdNm;

    @Column(name = "price")
    private Integer price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isinCd", referencedColumnName = "isinCd")
    private BondProductPrice bondProductPrice;


}
