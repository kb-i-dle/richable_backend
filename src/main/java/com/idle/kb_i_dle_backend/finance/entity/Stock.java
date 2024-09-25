package com.idle.kb_i_dle_backend.finance.entity;

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
    private int index;

    private int uid;

    private int pdno;

    private String prdt_name;

    private int hldg_qty;

    private String prod_category;

    private int add_date;

    private int delete_date;
}
