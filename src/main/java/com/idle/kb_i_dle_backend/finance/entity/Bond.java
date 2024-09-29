package com.idle.kb_i_dle_backend.finance.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

    @Column(name = "uid")
    private int uid;

    @Column(name = "itms_nm")
    private String itmsNm;

    @Column(name = "cnt")
    private int cnt;

    @Column(name = "prod_category")
    private String prodCategory;

    @Column(name = "per_price")
    private int perPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delete_date")
    private Date deleteDate;
}
